package cn.com.k12code.pigseckill.order.validator;

import cn.com.k12code.pigseckill.order.request.OrderCreateRequest;

/**
 * 订单校验
 * @author quxw
 */
public abstract class BaseOrderCreateValidator implements OrderCreateValidator{

	protected OrderCreateValidator nextValidator;

	@Override
	public void setNext(OrderCreateValidator nextValidator) {
		this.nextValidator = nextValidator;
	}

	@Override
	public OrderCreateValidator getNext() {
		return nextValidator;
	}

	@Override
	public void validate(OrderCreateRequest request) throws Exception {
		doValidate(request);

		if (nextValidator != null) {
			nextValidator.validate(request);
		}
	}

	/**
	 * 校验方法的具体实现
	 */
	protected abstract void doValidate(OrderCreateRequest request) throws Exception;

}
