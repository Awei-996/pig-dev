package cn.com.k12code.pigseckill.goods.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品实体类
 *
 * @author carl
 * @date 2025/01/23
 */
@Data
@TableName("sk_goods")
@Schema(description = "商品")
public class SkGoods implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 主键ID（雪花算法生成）
	 */
	@TableId(value = "id", type = IdType.ASSIGN_ID)
	@Schema(description = "主键ID")
	private String id;

	/**
	 * 创建时间
	 */
	@TableField(value = "gmt_create", fill = FieldFill.INSERT)
	@Schema(description = "创建时间")
	private LocalDateTime gmtCreate;

	/**
	 * 最后更新时间
	 */
	@TableField(value = "gmt_modified", fill = FieldFill.INSERT_UPDATE)
	@Schema(description = "最后更新时间")
	private LocalDateTime gmtModified;

	/**
	 * 商品名称
	 */
	@Schema(description = "商品名称")
	private String name;

	/**
	 * 商品封面
	 */
	@Schema(description = "商品封面")
	private String cover;

	/**
	 * 商品类目ID
	 */
	@Schema(description = "商品类目ID")
	private String classId;

	/**
	 * 价格
	 */
	@Schema(description = "价格")
	private BigDecimal price;

	/**
	 * 商品数量
	 */
	@Schema(description = "商品数量")
	private Long quantity;

	/**
	 * 详情
	 */
	@Schema(description = "详情")
	private String detail;

	/**
	 * 可销售库存
	 */
	@Schema(description = "可销售库存")
	private Long saleableInventory;


	/**
	 * 已占用库存
	 */
	@Schema(description = "已占用库存")
	private Long occupiedInventory;

	/**
	 * 冻结库存
	 */
	@Schema(description = "冻结库存")
	private Long frozenInventory;

	/**
	 * 状态
	 */
	@TableField(value = "state", fill = FieldFill.INSERT)
	@Schema(description = "状态")
	private String state;

	/**
	 * 商品创建时间
	 */
	@Schema(description = "商品创建时间")
	private LocalDateTime createTime;

	/**
	 * 商品发售时间
	 */
	@Schema(description = "商品发售时间")
	private LocalDateTime saleTime;

	/**
	 * 商品上链时间
	 */
	@Schema(description = "商品上链时间")
	private LocalDateTime syncChainTime;

	/**
	 * 预约开始时间
	 */
	@Schema(description = "预约开始时间")
	private LocalDateTime bookStartTime;

	/**
	 * 预约结束时间
	 */
	@Schema(description = "预约结束时间")
	private LocalDateTime bookEndTime;

	/**
	 * 结束时间
	 */
	@Schema(description = "结束时间")
	private LocalDateTime endTime;

	/**
	 * 是否可以预约
	 */
	@Schema(description = "是否可以预约")
	private Integer canBook;

	/**
	 * 是否逻辑删除，0为未删除，非0为已删除
	 */
	@TableLogic(value = "0", delval = "1")
	@TableField(fill = FieldFill.INSERT)
	@Schema(description = "是否逻辑删除，0为未删除，非0为已删除")
	private Integer deleted;

	/**
	 * 乐观锁版本号
	 */
	@Version
	@Schema(description = "乐观锁版本号")
	private Integer lockVersion;

	/**
	 * 创建者
	 */
	@Schema(description = "创建者")
	private String creatorId;

	/**
	 * 修改版本
	 */
	@Schema(description = "修改版本")
	private Integer version;

}
