package cn.com.k12code.pigseckill.order.validator;

import cn.com.k12code.pigseckill.order.request.OrderCreateRequest;

/**
 * 订单创建校验接口
 * @author quxw
 */
public interface OrderCreateValidator {
	/**
	 * 设置下一个校验器
	 * @param next 校验器
	 */
	void setNext(OrderCreateValidator next);

	/**
	 * 返回下一个校验器
	 * @return 校验器
	 */
	OrderCreateValidator getNext();

	/**
	 * 校验
	 * @param orderCreateRequest 订单创建参数
	 */
	void validate(OrderCreateRequest orderCreateRequest) throws Exception;
}
