package cn.com.k12code.pigseckill.goods.dto;

import cn.com.k12code.pigseckill.goods.enums.GoodsTypeEnum;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author quxw
 */
@Data
public class BuyDTO {

	@NotNull(message = "goodsId is null")
	private String goodsId;

	@NotNull(message = "goodsType is null")
	private GoodsTypeEnum goodsType;

	/**
	 * 商品数量
	 */
	@Min(value = 1)
	private int itemCount;

	/**
	 * 商品单价
	 */
	@DecimalMin(value = "0.0", inclusive = false, message = "商品单价必须大于0")
	private BigDecimal itemPrice;
}
