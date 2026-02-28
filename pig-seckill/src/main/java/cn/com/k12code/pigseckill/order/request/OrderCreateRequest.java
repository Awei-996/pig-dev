package cn.com.k12code.pigseckill.order.request;

import cn.com.k12code.pigseckill.order.enums.OrderEvent;
import cn.com.k12code.pigseckill.order.request.base.BaseOrderRequest;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * 订单创建
 * @author quxw
 */
@Getter
@Setter
public class OrderCreateRequest extends BaseOrderRequest {

	/**
	 * 买家id
	 */
	@NotNull(message = "买家id不能为空")
	private Long buyerId;

	/**
	 * 订单金额
	 */
	@DecimalMin(value = "0.0", inclusive = false, message = "订单金额必须大于0")
	private BigDecimal orderAmount;

	/**
	 * 商品Id
	 */
	@NotNull(message = "商品Id不能为空")
	private String goodsId;

	/**
	 * 商品数量
	 */
	@Min(value = 1)
	private Long itemCount;

	/**
	 * 商品单价
	 */
	@DecimalMin(value = "0.0", inclusive = false, message = "商品单价必须大于0")
	private BigDecimal itemPrice;

	/**
	 * 快照版本
	 */
	private Integer snapshotVersion;

	/**
	 * 交易订单号
	 */
	private String orderId;

	@Override
	public OrderEvent getOrderEvent() {
		return OrderEvent.CREATE;
	}
}
