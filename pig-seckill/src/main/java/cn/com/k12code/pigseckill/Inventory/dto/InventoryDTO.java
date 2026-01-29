package cn.com.k12code.pigseckill.Inventory.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author quxw
 */
@AllArgsConstructor
@Data
public class InventoryDTO {
	/**
	 * 商品ID
	 */
	@NotNull(message = "goods is null")
	private String goodsId;

	/**
	 * 商品ID
	 */
	@NotNull(message = "goodsType is null")
	private String goodsType;

	/**
	 * 库存数量
	 */
	private Long inventory;
}
