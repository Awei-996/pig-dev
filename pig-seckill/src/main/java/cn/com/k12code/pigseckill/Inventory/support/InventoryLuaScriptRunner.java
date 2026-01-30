package cn.com.k12code.pigseckill.Inventory.support;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RScript;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static com.pig4cloud.pig.common.core.constant.CacheConstants.*;

/**
 * 库存扣减/增加 Lua 脚本执行器。
 * 从 classpath:script/ 加载 decreaseLua.lua、increaseLua.lua，通过 Redisson 执行。
 *
 * @author quxw
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class InventoryLuaScriptRunner {

	private static final String DECREASE_SCRIPT_PATH = "script/decreaseLua.lua";
	private static final String INCREASE_SCRIPT_PATH = "script/increaseLua.lua";
	private static final String DECREASE = "DECREASE_";
	private static final String INCREASE = "INCREASE_";

	private final RedissonClient redissonClient;

	private String decreaseLuaScript;
	private String increaseLuaScript;

	@PostConstruct
	public void init() {
		this.decreaseLuaScript = loadScript(DECREASE_SCRIPT_PATH);
		this.increaseLuaScript = loadScript(INCREASE_SCRIPT_PATH);
	}

	private String loadScript(String path) {
		try {
			ClassPathResource resource = new ClassPathResource(path);
			try (InputStream is = resource.getInputStream()) {
				String script = new String(is.readAllBytes(), StandardCharsets.UTF_8);
				log.debug("Loaded Lua script from {}", path);
				return script;
			}
		} catch (IOException e) {
			log.error("Failed to load Lua script from {}", path, e);
			throw new IllegalStateException("Cannot load script: " + path, e);
		}
	}

	/**
	 * 执行库存扣减脚本。
	 *
	 * @param goodsId   商品ID
	 * @param decreaseBy 扣减数量
	 * @param identifier 操作人/订单ID，用于防重（同一 operatorId 不能重复扣减）
	 * @return 扣减后的库存数量
	 * @throws InventoryScriptException 脚本执行失败（如库存不足、已执行过等）
	 */
	public Long executeDecrease(String goodsId, long decreaseBy, String identifier) {
		String inventoryKey = INVENTORY_KEY + goodsId;
		String streamKey = INVENTORY_STREAM_KEY + goodsId;
		List<Object> keys = List.of(inventoryKey, streamKey);

		RScript script = redissonClient.getScript(StringCodec.INSTANCE);
		try {
			Object result = script.eval(
					RScript.Mode.READ_WRITE,
					decreaseLuaScript,
					RScript.ReturnType.INTEGER,
					keys,
					String.valueOf(decreaseBy),
					DECREASE + identifier
			);
			return result != null ? ((Number) result).longValue() : null;
		} catch (Exception e) {
			throw mapDecreaseException(e);
		}
	}

	/**
	 * 执行库存增加脚本。
	 *
	 * @param goodsId    商品ID
	 * @param increaseBy 增加数量
	 * @param identifier 幂等标识，用于防重（同一 identifier 不能重复增加）
	 * @return 增加后的库存数量
	 * @throws InventoryScriptException 脚本执行失败（如 key 不存在、已执行过等）
	 */
	public Long executeIncrease(String goodsId, long increaseBy, String identifier) {
		String inventoryKey = INVENTORY_KEY + goodsId;
		String streamKey = INVENTORY_STREAM_KEY + goodsId;
		List<Object> keys = List.of(inventoryKey, streamKey);

		RScript script = redissonClient.getScript(StringCodec.INSTANCE);
		try {
			Object result = script.eval(
					RScript.Mode.READ_WRITE,
					increaseLuaScript,
					RScript.ReturnType.INTEGER,
					keys,
					String.valueOf(increaseBy),
					INCREASE + identifier
			);
			return result != null ? ((Number) result).longValue() : null;
		} catch (Exception e) {
			throw mapIncreaseException(e);
		}
	}

	private InventoryScriptException mapDecreaseException(Exception e) {
		String msg = e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName();
		if (msg.contains("OPERATION_ALREADY_EXECUTED")) {
			return new InventoryScriptException(InventoryScriptException.Reason.OPERATION_ALREADY_EXECUTED, msg);
		}
		if (msg.contains("KEY_NOT_FOUND")) {
			return new InventoryScriptException(InventoryScriptException.Reason.KEY_NOT_FOUND, msg);
		}
		if (msg.contains("INVENTORY_IS_ZERO")) {
			return new InventoryScriptException(InventoryScriptException.Reason.INVENTORY_IS_ZERO, msg);
		}
		if (msg.contains("INVENTORY_NOT_ENOUGH")) {
			return new InventoryScriptException(InventoryScriptException.Reason.INVENTORY_NOT_ENOUGH, msg);
		}
		if (msg.contains("current value is not a number")) {
			return new InventoryScriptException(InventoryScriptException.Reason.INVALID_VALUE, msg);
		}
		log.warn("Decrease script error: {}", msg);
		return new InventoryScriptException(InventoryScriptException.Reason.UNKNOWN, msg);
	}

	private InventoryScriptException mapIncreaseException(Exception e) {
		String msg = e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName();
		if (msg.contains("OPERATION_ALREADY_EXECUTED")) {
			return new InventoryScriptException(InventoryScriptException.Reason.OPERATION_ALREADY_EXECUTED, msg);
		}
		if (msg.contains("KEY_NOT_FOUND") || msg.contains("key not found")) {
			return new InventoryScriptException(InventoryScriptException.Reason.KEY_NOT_FOUND, msg);
		}
		if (msg.contains("current value is not a number")) {
			return new InventoryScriptException(InventoryScriptException.Reason.INVALID_VALUE, msg);
		}
		log.warn("Increase script error: {}", msg);
		return new InventoryScriptException(InventoryScriptException.Reason.UNKNOWN, msg);
	}
}
