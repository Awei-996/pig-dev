package cn.com.k12code.pigseckill.utils;

import cn.com.k12code.pigseckill.config.WorkerIdHolder;
import cn.com.k12code.pigseckill.goods.enums.GoodsOrderPrefixEnum;
import cn.com.k12code.pigseckill.goods.enums.GoodsTypeEnum;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicReference;

/**
 * 基于 Snowflake 的分布式 ID 生成器。
 * <p>
 * workerId 优先从 {@link WorkerIdHolder} 获取（Redis 租约式槽位分配 0~31，带心跳续期与停机释放）；
 * 若 Redis 未分配成功，则从配置 {@code snowflake.worker-id} 或环境变量/系统属性读取。
 * <p>
 * 使用方式：多实例部署时依赖 WorkerIdHolder 自动分配；单机或测试可配置 snowflake.worker-id 或环境变量。
 *
 * @author quxw
 */
@Component
@DependsOn("workerIdHolder")
public class SnowflakeIdGenerator {

	private static final Logger log = LoggerFactory.getLogger(SnowflakeIdGenerator.class);
	private static final int MAX_WORKER_ID = 31;
	private static final long DATACENTER_ID = 0L;

	private static final AtomicReference<Snowflake> SNOWFLAKE_REF = new AtomicReference<>(createDefaultSnowflake());

	@PostConstruct
	public void init() {
		long w = WorkerIdHolder.WORKER_ID;
		if (w < 0) {
			w = getWorkerIdFromEnv();
		}
		w = clamp(w, 0, MAX_WORKER_ID);
		Snowflake snowflake = IdUtil.getSnowflake(w, DATACENTER_ID);
		SNOWFLAKE_REF.set(snowflake);
		log.info("SnowflakeIdGenerator 已初始化, workerId={}", w);
	}

	private SnowflakeIdGenerator() {}

	/**
	 * 生成分布式 ID 字符串。
	 */
	public static String nextId() {
		return StrUtil.toString(SNOWFLAKE_REF.get().nextId());
	}

	/**
	 * 根据商品类型生成带前缀的订单号，格式：订单前缀（10/11/...）+ 数字 ID。
	 * 前缀由 {@link GoodsOrderPrefixEnum} 根据 {@link GoodsTypeEnum} 转换得到。
	 *
	 * @param goodsType 商品类型，为 null 时仅返回数字 ID（无前缀）
	 * @return 带类型前缀的订单号，如 "10" + 雪花 ID
	 */
	public static String nextId(GoodsTypeEnum goodsType) {
		GoodsOrderPrefixEnum prefixEnum = GoodsOrderPrefixEnum.fromGoodsType(goodsType);
		String prefix = prefixEnum != null ? prefixEnum.getPrefixStr() : "";
		return prefix + SNOWFLAKE_REF.get().nextId();
	}

	private static long getWorkerIdFromEnv() {
		String v = System.getProperty("snowflake.worker-id");
		if (v == null || v.isEmpty()) {
			v = System.getenv("SNOWFLAKE_WORKER_ID");
		}
		if (v != null && !v.isEmpty()) {
			try {
				return clamp(Long.parseLong(v.trim()), 0, MAX_WORKER_ID);
			} catch (NumberFormatException e) {
				log.warn("snowflake worker-id 配置无效，将使用默认值 0: {}", v);
			}
		}
		return 0L;
	}

	/**
	 * 限制值在 min 和 max 之间
	 * @param value 值
	 * @param min 最小值
	 * @param max 最大值
	 * @return 限制后的值
	 */
	private static long clamp(long value, long min, long max) {
		return Math.max(min, Math.min(max, value));
	}

	/**
	 * 创建默认雪花算法实例
	 * @return 雪花算法实例
	 */
	private static Snowflake createDefaultSnowflake() {
		long w = getWorkerIdFromEnv();
		return IdUtil.getSnowflake(w, 0L);
	}
}
