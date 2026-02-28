package cn.com.k12code.pigseckill.order.service.impl;

import cn.com.k12code.pigseckill.order.entity.SkGoodsOrder;
import cn.com.k12code.pigseckill.order.enums.OrderState;
import cn.com.k12code.pigseckill.order.mapper.SkGoodsOrderMapper;
import cn.com.k12code.pigseckill.order.request.OrderCreateAndConfirmRequest;
import cn.com.k12code.pigseckill.order.response.OrderResponse;
import cn.com.k12code.pigseckill.order.service.SkGoodsOrderService;
import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pig4cloud.pig.common.core.constant.CommonConstants;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author quxw
 */
@Service
public class SkGoodsOrderServiceImpl extends ServiceImpl<SkGoodsOrderMapper, SkGoodsOrder> implements SkGoodsOrderService {

	@Override
	public OrderResponse createAndConfirm(OrderCreateAndConfirmRequest orderCreateAndConfirmRequest) {
		// 查询订单是否存在
		List<SkGoodsOrder> list = this.lambdaQuery()
				.eq(SkGoodsOrder::getBuyerId, orderCreateAndConfirmRequest.getBuyerId())
				.eq(SkGoodsOrder::getIdentifier, orderCreateAndConfirmRequest.getIdentifier())
				.list();

		if (CollUtil.isEmpty(list)) {
			return (OrderResponse) new OrderResponse()
					.setOrderId(orderCreateAndConfirmRequest.getOrderId())
					.setCode(CommonConstants.SUCCESS)
					.setMsg("当前订单创建成功");
		}

		// 存储
		SkGoodsOrder skGoodsOrder = new SkGoodsOrder()
				.setGoodsId(orderCreateAndConfirmRequest.getGoodsId())
				.setBuyerId(orderCreateAndConfirmRequest.getBuyerId().toString())
				.setIdentifier(orderCreateAndConfirmRequest.getIdentifier())
				.setItemCount(Math.toIntExact(orderCreateAndConfirmRequest.getItemCount()))
				.setItemPrice(orderCreateAndConfirmRequest.getItemPrice())
				.setOrderAmount(orderCreateAndConfirmRequest.getOrderAmount())
				.setOrderConfirmedTime(orderCreateAndConfirmRequest.getOperateTime())
				.setOrderState(OrderState.CONFIRM)
				.setPaidAmount(BigDecimal.ZERO);
		boolean save = this.save(skGoodsOrder);
		if (!save) {
			return (OrderResponse) new OrderResponse()
					.setOrderId(orderCreateAndConfirmRequest.getOrderId())
					.setCode(CommonConstants.FAIL)
					.setMsg("订单创建失败");
		}

		return (OrderResponse) new OrderResponse()
				.setOrderId(orderCreateAndConfirmRequest.getOrderId())
				.setCode(CommonConstants.SUCCESS)
				.setMsg("订单创建成功");
	}
}
