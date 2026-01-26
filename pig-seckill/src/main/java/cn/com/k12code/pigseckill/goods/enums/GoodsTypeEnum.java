package cn.com.k12code.pigseckill.goods.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 商品类型枚举
 *
 * @author carl
 * @date 2025/01/26
 */
@Getter
@AllArgsConstructor
public enum GoodsTypeEnum {

	/**
	 * 实物商品
	 */
	PHYSICAL("wine", "实物商品"),

	/**
	 * 虚拟商品
	 */
	VIRTUAL("nft", "虚拟商品");


	/**
	 * 类型码
	 */
	private final String code;

	/**
	 * 类型描述
	 */
	private final String desc;

	/**
	 * 根据类型码获取枚举
	 * @param code 类型码
	 * @return 枚举对象
	 */
	public static GoodsTypeEnum getByCode(String code) {
		for (GoodsTypeEnum type : values()) {
			if (type.getCode().equals(code)) {
				return type;
			}
		}
		return null;
	}

	/**
	 * 判断类型码是否有效
	 * @param code 类型码
	 * @return 是否有效
	 */
	public static boolean isValid(String code) {
		return getByCode(code) != null;
	}

}
