package cn.com.k12code.pigseckill.order.validator.impl;

import cn.com.k12code.pigseckill.goods.entity.SkGoods;
import cn.com.k12code.pigseckill.goods.enums.GoodsStateEnum;
import cn.com.k12code.pigseckill.goods.service.SkGoodsService;
import cn.com.k12code.pigseckill.order.request.OrderCreateRequest;
import cn.com.k12code.pigseckill.order.validator.BaseOrderCreateValidator;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * @author quxw
 */

@AllArgsConstructor
@NoArgsConstructor
public class GoodsValidator extends BaseOrderCreateValidator {

	private SkGoodsService skGoodsService;

	@Override
	protected void doValidate(OrderCreateRequest request) throws Exception {
		SkGoods skGoods = skGoodsService.getGoodsById(request.getGoodsId());
		if (skGoods != null) {
			String state = skGoods.getState();

			if (state.equals(GoodsStateEnum.FREEZE.getCode())) {
				throw new Exception("当前商品已冻结");
			}
			if (state.equals(GoodsStateEnum.SOLD_OUT.getCode())) {
				throw new Exception("当前商品已售完");
			}

			if (skGoods.getPrice().compareTo(request.getItemPrice()) != 0) {
				throw new Exception("当前商品价格发生变化");
			}
		} else {
			throw new Exception("商品不存在");
		}
	}
}
