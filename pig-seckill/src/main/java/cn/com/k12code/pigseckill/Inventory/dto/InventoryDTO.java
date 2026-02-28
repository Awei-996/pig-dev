package cn.com.k12code.pigseckill.Inventory.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author quxw
 */
@Accessors(chain = true)
@Data
@NoArgsConstructor
public class InventoryDTO {
	/**
	 * 商品ID
	 */
	@NotNull(message = "goods is null")
	private String goodsId;

	/**
	 * 商品类型
	 */
	@NotNull(message = "goodsType is null")
	private String goodsType;

	/**
	 * 库存数量（初始化时表示初始库存，扣减时表示扣减数量）
	 */
	private Long inventory;

	/**
	 * 操作人/订单ID（扣减时必填，用于防重：同一 identifier 不能重复扣减）
	 */
	private String identifier;

}
