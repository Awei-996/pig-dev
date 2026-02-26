package cn.com.k12code.pigseckill.order.config;

import cn.com.k12code.pigseckill.order.validator.OrderCreateValidator;
import cn.com.k12code.pigseckill.order.validator.impl.GoodsValidator;
import cn.com.k12code.pigseckill.order.validator.impl.UserValidator;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * 订单相关配置
 * @author quxw
 */
@Configuration
public class OrderConfiguration {


	@Bean
	@Scope(value = BeanDefinition.SCOPE_PROTOTYPE)
	public UserValidator userValidator() {
		return new UserValidator();
	}

	@Bean
	@Scope(value = BeanDefinition.SCOPE_PROTOTYPE)
	public GoodsValidator goodsValidator() {
		return new GoodsValidator();
	}

	@Bean
	public OrderCreateValidator orderValidatorChain(UserValidator userValidator, GoodsValidator goodsValidator) {
		userValidator.setNext(goodsValidator);
		return userValidator;
	}
}
