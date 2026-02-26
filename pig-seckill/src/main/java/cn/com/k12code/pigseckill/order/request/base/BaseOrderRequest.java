package cn.com.k12code.pigseckill.order.request.base;

import cn.com.k12code.pigseckill.order.enums.OrderEvent;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author quxw
 */
@Getter
@Setter
public abstract class BaseOrderRequest implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

	/**
	 * 幂等信息
	 */
	@NotNull(message = "identifier 不能为空")
	private String identifier;

	/**
	 * 获取订单时间
	 * @return 订单状态
	 */
	public abstract OrderEvent getOrderEvent();

}
