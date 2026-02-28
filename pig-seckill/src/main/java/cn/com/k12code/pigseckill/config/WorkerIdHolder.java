package cn.com.k12code.pigseckill.config;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.UUID;

/**
 * 通过 Redis 租约机制为当前实例分配 Snowflake workerId（0~31）。
 * <p>
 * 每个 workerId 槽位对应一个带 TTL 的 Redis Key，实例启动时抢占空闲槽位，
 * 运行期间通过定时心跳续期保持租约，停止时主动释放。
 * <p>
 * 相比简单 INCR 取模方案：
 * <ul>
 *   <li>槽位用完后可自动回收已停止实例的 workerId，不会因重启累积导致冲突</li>
 *   <li>实例正常停止时 {@code @PreDestroy} 立即释放，异常宕机靠 TTL 过期自动释放</li>
 *   <li>心跳续期保证运行中的实例不会丢失槽位</li>
 * </ul>
 *
 * @see cn.com.k12code.pigseckill.utils.SnowflakeIdGenerator
 */
@Component
public class WorkerIdHolder {

	private static final Logger log = LoggerFactory.getLogger(WorkerIdHolder.class);

	private static final int MAX_WORKER_ID = 31;
	private static final Duration LEASE_TTL = Duration.ofSeconds(30);
	private static final String KEY_PREFIX = "snowflake:worker:";

	private static final String INSTANCE_ID = UUID.randomUUID().toString();

	@Value("${order.client.name:workerId}")
	private String clientName;

	/**
	 * 当前实例的 workerId（0~31）。未成功从 Redis 分配时为 -1，
	 * SnowflakeIdGenerator 会回退到配置/环境变量。
	 */
	public static volatile long WORKER_ID = -1L;

	private final RedissonClient redissonClient;

	private volatile String acquiredKey;

	public WorkerIdHolder(RedissonClient redissonClient) {
		this.redissonClient = redissonClient;
	}

	@PostConstruct
	public void init() {
		try {
			for (int i = 0; i <= MAX_WORKER_ID; i++) {
				String key = buildKey(i);
				RBucket<String> bucket = redissonClient.getBucket(key);
				boolean acquired = bucket.setIfAbsent(INSTANCE_ID, LEASE_TTL);
				if (acquired) {
					WORKER_ID = i;
					acquiredKey = key;
					log.info("snowflake workerId 已通过租约分配: key={}, workerId={}, instanceId={}", key, i, INSTANCE_ID);
					return;
				}
			}
			log.warn("所有 workerId 槽位(0~31)均被占用，将使用配置/环境变量回退");
			WORKER_ID = -1L;
		} catch (Exception e) {
			log.warn("从 Redis 分配 workerId 失败，将使用配置/环境变量回退", e);
			WORKER_ID = -1L;
		}
	}

	/**
	 * 定时心跳：每 10 秒续期一次租约（TTL 30 秒），保证运行中的实例不会丢失槽位。
	 */
	@Scheduled(fixedRate = 10_000)
	public void renewLease() {
		if (acquiredKey == null) {
			return;
		}
		try {
			RBucket<String> bucket = redissonClient.getBucket(acquiredKey);
			String holder = bucket.get();
			if (INSTANCE_ID.equals(holder)) {
				bucket.expire(LEASE_TTL);
			} else {
				log.warn("workerId 租约已被其他实例覆盖, key={}, 当前持有者={}", acquiredKey, holder);
				acquiredKey = null;
			}
		} catch (Exception e) {
			log.warn("workerId 租约续期失败, key={}", acquiredKey, e);
		}
	}

	/**
	 * 实例正常停止时主动释放槽位，使其他实例可以立即使用。
	 */
	@PreDestroy
	public void release() {
		if (acquiredKey == null) {
			return;
		}
		try {
			RBucket<String> bucket = redissonClient.getBucket(acquiredKey);
			String holder = bucket.get();
			if (INSTANCE_ID.equals(holder)) {
				bucket.delete();
				log.info("snowflake workerId 已释放: key={}, workerId={}", acquiredKey, WORKER_ID);
			}
		} catch (Exception e) {
			log.warn("释放 workerId 失败, key={}", acquiredKey, e);
		}
	}

	private String buildKey(int workerId) {
		return KEY_PREFIX + clientName + ":" + workerId;
	}
}
