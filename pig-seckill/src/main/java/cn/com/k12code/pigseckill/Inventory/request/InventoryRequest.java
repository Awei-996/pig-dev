package cn.com.k12code.pigseckill.Inventory.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * 库存请求信息
 * @author quxw
 */
@Accessors(chain = true)
@Data
public class InventoryRequest implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

	@NotNull(message = "goods is null")
	private String goodsId;

	private String identifier;

	private Integer inventory;

}
