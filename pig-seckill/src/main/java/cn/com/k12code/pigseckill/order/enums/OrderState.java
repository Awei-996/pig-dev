package cn.com.k12code.pigseckill.order.enums;

/**
 * @author quxw
 */
public enum OrderState {

	/**
	 * 订单创建
	 */
	CREATE,

	/**
	 * 订单确认
	 */
	CONFIRM,
	/**
	 * 已付款
	 */
	PAID,
	/**
	 * 交易成功
	 */
	FINISH,
	/**
	 * 订单关闭
	 */
	CLOSED,
	/**
	 * 废单，用户看不到
	 */
	DISCARD;
}
