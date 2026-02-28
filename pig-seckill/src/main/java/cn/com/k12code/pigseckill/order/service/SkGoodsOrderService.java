package cn.com.k12code.pigseckill.order.service;

import cn.com.k12code.pigseckill.order.entity.SkGoodsOrder;
import cn.com.k12code.pigseckill.order.request.OrderCreateAndConfirmRequest;
import cn.com.k12code.pigseckill.order.response.OrderResponse;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author quxw
 */
public interface SkGoodsOrderService extends IService<SkGoodsOrder> {

	OrderResponse createAndConfirm(OrderCreateAndConfirmRequest orderCreateAndConfirmRequest);
}
