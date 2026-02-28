package cn.com.k12code.pigseckill.order.response;

import com.pig4cloud.pig.common.core.util.R;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author quxw
 */
@Accessors(chain = true)
@Data
public class OrderResponse extends R {

	private String orderId;

}
