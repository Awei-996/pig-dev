package cn.com.k12code.pigseckill.Inventory.support;

import lombok.Getter;

/**
 * 库存 Lua 脚本执行异常。
 *
 * @author quxw
 */
@Getter
public class InventoryScriptException extends RuntimeException {

	private final Reason reason;

	public InventoryScriptException(Reason reason, String message) {
		super(message);
		this.reason = reason;
	}

	public enum Reason {
		/** 同一操作人/订单已执行过扣减，防重 */
		OPERATION_ALREADY_EXECUTED,
		/** 库存 key 不存在（未初始化） */
		KEY_NOT_FOUND,
		/** 库存已为 0 */
		INVENTORY_IS_ZERO,
		/** 库存不足 */
		INVENTORY_NOT_ENOUGH,
		/** 当前值不是数字 */
		INVALID_VALUE,
		/** 其他错误 */
		UNKNOWN
	}
}
