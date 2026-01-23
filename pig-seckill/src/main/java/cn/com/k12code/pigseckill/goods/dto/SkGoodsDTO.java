package cn.com.k12code.pigseckill.goods.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品查询DTO
 *
 * @author carl
 * @date 2025/01/23
 */
@Data
@Schema(description = "商品查询对象")
public class SkGoodsDTO {

	/**
	 * 主键ID
	 */
	@Schema(description = "主键ID")
	private Long id;

	/**
	 * 商品名称
	 */
	@Schema(description = "商品名称")
	private String name;

	/**
	 * 商品类目ID
	 */
	@Schema(description = "商品类目ID")
	private String classId;

	/**
	 * 状态
	 */
	@Schema(description = "状态")
	private String state;

	/**
	 * 是否可以预约
	 */
	@Schema(description = "是否可以预约")
	private Integer canBook;

	/**
	 * 价格范围 - 最低价
	 */
	@Schema(description = "最低价格")
	private BigDecimal minPrice;

	/**
	 * 价格范围 - 最高价
	 */
	@Schema(description = "最高价格")
	private BigDecimal maxPrice;

	/**
	 * 商品创建时间范围 [开始时间，结束时间]
	 */
	@Schema(description = "商品创建时间范围")
	private LocalDateTime[] createTime;

	/**
	 * 商品发售时间范围 [开始时间，结束时间]
	 */
	@Schema(description = "商品发售时间范围")
	private LocalDateTime[] saleTime;

	/**
	 * 预约开始时间范围 [开始时间，结束时间]
	 */
	@Schema(description = "预约开始时间范围")
	private LocalDateTime[] bookStartTime;

	/**
	 * 预约结束时间范围 [开始时间，结束时间]
	 */
	@Schema(description = "预约结束时间范围")
	private LocalDateTime[] bookEndTime;

}
