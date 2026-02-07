package cn.com.k12code.pigseckill.config;

import jakarta.annotation.PostConstruct;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 通过 Redis 原子自增为当前实例分配 Snowflake workerId（0~31）。
 * <p>
 * 使用服务名（或配置的 clientName）作为 Redis key，同一服务多实例启动时依次自增取模，
 * 保证同服务内各实例 workerId 不同；不同服务使用不同 key，互不影响。
 * <p>
 * 使用方式：优先配置 {@code order.client.name}（建议与 spring.application.name 一致），
 * 不配时默认 "workerId"。SnowflakeIdGenerator 会在初始化时从此处读取 workerId。
 * <p>
 * <b>优点</b>：无需在配置文件或环境里为每个实例单独配 worker-id，多实例扩容时自动分配，按服务名隔离。
 * <p>
 * <b>缺点/注意</b>：（1）依赖 Redis，Redis 不可用时回退到配置/环境变量；（2）取模复用：
 * 同一 key 下第 33 个实例会得到与第 1 个相同的 workerId，故同服务实例数不宜超过 32；
 * （3）实例下线不会回收编号，仅靠自增取模，若需严格“下线即回收”可改为占位+TTL 等方案。
 *
 * @see cn.com.k12code.pigseckill.utils.SnowflakeIdGenerator
 */
@Component
public class WorkerIdHolder {

	private static final Logger log = LoggerFactory.getLogger(WorkerIdHolder.class);
	private static final int WORKER_ID_MOD = 32;

	/**
	 * Redis 自增 key 前缀/服务标识，同一服务多实例共享同一 key 以分配不同 workerId。
	 * 建议与 spring.application.name 一致，便于多服务部署时按服务区分。
	 */
	@Value("${order.client.name:workerId}")
	private String clientName;

	/**
	 * 当前实例的 workerId，0~31。未成功从 Redis 分配时为 -1，SnowflakeIdGenerator 会回退到配置/环境变量。
	 */
	public static long WORKER_ID = -1L;

	private final RedissonClient redissonClient;

	public WorkerIdHolder(RedissonClient redissonClient) {
		this.redissonClient = redissonClient;
	}

	@PostConstruct
	public void init() {
		String key = "snowflake:workerId:" + clientName;
		try {
			RAtomicLong atomicLong = redissonClient.getAtomicLong(key);
			long next = atomicLong.incrementAndGet();
			WORKER_ID = next % WORKER_ID_MOD;
			log.info("snowflake workerId 已从 Redis 分配: key={}, sequence={}, workerId={}", key, next, WORKER_ID);
		} catch (Exception e) {
			log.warn("从 Redis 分配 workerId 失败，将使用配置/环境变量回退: key={}", key, e);
			WORKER_ID = -1L;
		}
	}
}
