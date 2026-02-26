package cn.com.k12code.pigseckill.streammq.producer;

import cn.com.k12code.pigseckill.streammq.param.MessageBody;
import cn.hutool.core.lang.UUID;
import com.alibaba.fastjson.JSON;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

/**
 * @author quxw
 */
@RequiredArgsConstructor
@Component
public class StreamProducer {

	private static final Logger logger = LoggerFactory.getLogger(StreamProducer.class);

	private static final String TAG = "TAGS";

	private final StreamBridge streamBridge;

	/**
	 * 发送消息
	 * @param bingingName 通道名字
	 * @param tag 标识
	 * @param message 消息
	 * @return 状态
	 */
	private boolean sendMessage(String bingingName,String tag,String message){

		logger.info("send message : {} , {} , {}", bingingName, tag, JSON.toJSONString(message));

		Message<MessageBody> bodyMessage = MessageBuilder
				.withPayload(
					new MessageBody()
							.setIdentifier(UUID.randomUUID().toString())
							.setBody(message)
				)
				.setHeader(TAG, tag)
				.build();

		boolean result = streamBridge.send(bingingName, bodyMessage);
		logger.info("send result : {} , {} , {}", bingingName, tag, result);
		return result;
	}
}
