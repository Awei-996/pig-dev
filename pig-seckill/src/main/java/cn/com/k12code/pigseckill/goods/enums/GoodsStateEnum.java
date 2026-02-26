package cn.com.k12code.pigseckill.goods.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 商品状态枚举
 *
 * @author carl
 * @date 2025/01/23
 */
@Getter
@AllArgsConstructor
public enum GoodsStateEnum {

	/**
	 * 上架
	 */
	ON_SALE("ON_SALE", "上架"),

	/**
	 * 下架
	 */
	OFF_SALE("OFF_SALE", "下架"),

	/**
	 * 待审核
	 */
	PENDING("PENDING", "待审核"),

	/**
	 * 驳回
	 */
	REJECTED("REJECTED", "驳回"),

	/**
	 * 售完
	 */
	SOLD_OUT("SOLD_OUT","售完"),

	/**
	 * 冻结
	 */
	FREEZE("FREEZE","冻结");

	/**
	 * 状态码
	 */
	private final String code;

	/**
	 * 状态描述
	 */
	private final String desc;

	/**
	 * 根据状态码获取枚举
	 * @param code 状态码
	 * @return 枚举对象
	 */
	public static GoodsStateEnum getByCode(String code) {
		for (GoodsStateEnum state : values()) {
			if (state.getCode().equals(code)) {
				return state;
			}
		}
		return null;
	}

	/**
	 * 判断状态码是否有效
	 * @param code 状态码
	 * @return 是否有效
	 */
	public static boolean isValid(String code) {
		return getByCode(code) != null;
	}

}
