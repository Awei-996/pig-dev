package cn.com.k12code.pigseckill.streammq.param;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author quxw
 */
@Data
@Accessors(chain = true)
public class MessageBody{
	/**
	 * 幂等号
	 */
	private String identifier;
	/**
	 * 消息体
	 */
	private String body;
}
