package cn.com.k12code.pigseckill.goods.listener;

import cn.com.k12code.pigseckill.goods.service.SkGoodsService;
import cn.com.k12code.pigseckill.order.entity.SkGoodsOrder;
import cn.com.k12code.pigseckill.order.request.OrderCreateAndConfirmRequest;
import cn.com.k12code.pigseckill.order.service.SkGoodsOrderService;
import com.alibaba.fastjson2.JSON;
import com.pig4cloud.pig.common.core.constant.CommonConstants;
import com.pig4cloud.pig.common.core.util.R;
import lombok.RequiredArgsConstructor;
import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 订单创建事务处理
 * @author quxw
 */
@RequiredArgsConstructor
@Component
public class OrderCreateTransactionListener implements TransactionListener {

	private final SkGoodsService skGoodsService;
	private final SkGoodsOrderService skGoodsOrderService;

	@Override
	public LocalTransactionState executeLocalTransaction(Message message, Object o) {
		try {
			OrderCreateAndConfirmRequest orderCreateAndConfirmRequest = JSON.parseObject(JSON.parseObject(message.getBody()).getString("body"), OrderCreateAndConfirmRequest.class);
			R<?> result = skGoodsService.buyGood(orderCreateAndConfirmRequest);

			return CommonConstants.SUCCESS.equals(result.getCode()) ?LocalTransactionState.COMMIT_MESSAGE : LocalTransactionState.ROLLBACK_MESSAGE;
		} catch (Exception e){
			return LocalTransactionState.ROLLBACK_MESSAGE;
		}
	}

	@Override
	public LocalTransactionState checkLocalTransaction(MessageExt messageExt) {
		OrderCreateAndConfirmRequest orderCreateAndConfirmRequest = JSON.parseObject(JSON.parseObject(new String(messageExt.getBody())).getString("body"), OrderCreateAndConfirmRequest.class);
		List<SkGoodsOrder> list = skGoodsOrderService.lambdaQuery()
				.eq(SkGoodsOrder::getOrderId, orderCreateAndConfirmRequest.getOrderId())
				.list();
		if (!list.isEmpty()) {
			return LocalTransactionState.COMMIT_MESSAGE;
		}
		return LocalTransactionState.ROLLBACK_MESSAGE;
	}
}
