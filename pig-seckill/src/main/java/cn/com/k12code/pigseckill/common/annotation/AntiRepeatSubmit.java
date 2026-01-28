package cn.com.k12code.pigseckill.common.annotation;

import java.lang.annotation.*;

/**
 * 防重复提交注解
 * <p>
 * 用于标记需要防重复提交的接口方法
 * </p>
 *
 * @author quxw
 * @date 2025/01/26
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AntiRepeatSubmit {

	/**
	 * Token参数名称，默认从请求头 X-Submit-Token 获取
	 * 如果请求头中没有，则从请求参数中获取该名称的参数
	 * @return Token参数名称
	 */
	String tokenParam() default "X-Submit-Token";


}
