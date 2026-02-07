package cn.com.k12code.pigseckill.goods.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 商品订单号前缀枚举，与 {@link GoodsTypeEnum} 一一对应，前缀从 10 递增。
 * 用于生成带类型前缀的订单号，便于区分实物/虚拟等订单。
 *
 * @author carl
 */
@Getter
@AllArgsConstructor
public enum GoodsOrderPrefixEnum {

	/**
	 * 实物商品订单
	 */
	PHYSICAL(10, GoodsTypeEnum.PHYSICAL),

	/**
	 * 虚拟商品订单
	 */
	VIRTUAL(11, GoodsTypeEnum.VIRTUAL);

	/**
	 * 订单号前缀（数字，从 10 递增）
	 */
	private final int prefix;

	/**
	 * 对应商品类型
	 */
	private final GoodsTypeEnum goodsType;

	/**
	 * 获取前缀字符串，用于拼接订单号
	 */
	public String getPrefixStr() {
		return String.valueOf(prefix);
	}

	/**
	 * 根据商品类型获取订单前缀枚举
	 *
	 * @param goodsType 商品类型
	 * @return 订单前缀枚举，未匹配时返回 null
	 */
	public static GoodsOrderPrefixEnum fromGoodsType(GoodsTypeEnum goodsType) {
		if (goodsType == null) {
			return null;
		}
		for (GoodsOrderPrefixEnum e : values()) {
			if (e.getGoodsType() == goodsType) {
				return e;
			}
		}
		return null;
	}
}
