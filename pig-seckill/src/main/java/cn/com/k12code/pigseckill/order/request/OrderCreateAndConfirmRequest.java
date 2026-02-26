package cn.com.k12code.pigseckill.order.request;

import cn.com.k12code.pigseckill.order.enums.OrderEvent;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * @author quxw
 */
@Getter
@Setter
public class OrderCreateAndConfirmRequest extends OrderCreateRequest {

	/**
	 * 操作时间
	 */
	@NotNull(message = "operateTime 不能为空")
	private LocalDateTime operateTime;

	/**
	 * 是否同步扣减库存
	 */
	private boolean syncDecreaseInventory = false;

	@Override
	public OrderEvent getOrderEvent() {
		return OrderEvent.CREATE_AND_CONFIRM;
	}
}
