package cn.com.k12code.pigseckill.order.entity;

import cn.com.k12code.pigseckill.order.enums.OrderState;
import com.pig4cloud.pig.common.mybatis.base.BaseEntity;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单信息
 * @author quxw
 */
@Accessors(chain = true)
@Data
public class SkGoodsOrder extends BaseEntity {
	/**
	 * 主键ID
	 */
	private Long id;


	/**
	 * 订单号
	 */
	private String orderId;

	/**
	 * 买家ID
	 */
	private String buyerId;

	/**
	 * 幂等号
	 */
	private String identifier;

	/**
	 * 商品ID
	 */
	private String goodsId;

	/**
	 * 商品单价
	 */
	private BigDecimal itemPrice;

	/**
	 * 商品数量
	 */
	private Integer itemCount;

	/**
	 * 订单金额
	 */
	private BigDecimal orderAmount;

	/**
	 * 订单状态
	 */
	private OrderState orderState;

	/**
	 * 已支付金额
	 */
	private BigDecimal paidAmount;

	/**
	 * 支付成功时间
	 */
	private LocalDateTime paySucceedTime;

	/**
	 * 订单确认时间
	 */
	private LocalDateTime orderConfirmedTime;

	/**
	 * 完结时间
	 */
	private LocalDateTime orderFinishedTime;

	/**
	 * 关单时间
	 */
	private LocalDateTime orderClosedTime;

	/**
	 * 支付方式
	 */
	private String payChannel;

	/**
	 * 支付流水号
	 */
	private String payStreamId;

	/**
	 * 关闭类型
	 */
	private String closeType;

	/**
	 * 逻辑删除标识 (0:未删除, 1:已删除)
	 */
	private Integer deleted;

	/**
	 * 乐观锁版本号
	 */
	private Integer lockVersion;

	/**
	 * 商品快照版本号
	 */
	private Integer snapshotVersion;

}
