package cn.com.k12code.pigseckill.config;

import cn.com.k12code.pigseckill.common.interceptor.AntiRepeatSubmitInterceptor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type.SERVLET;

/**
 * 秒杀服务WebMvc配置
 * <p>
 * 注册防重复提交拦截器
 * </p>
 *
 * @author quxw
 * @date 2025/01/26
 */
@AutoConfiguration
@ConditionalOnWebApplication(type = SERVLET)
public class SeckillWebMvcConfiguration implements WebMvcConfigurer {

	/**
	 * 添加拦截器
	 * @param registry 拦截器注册器
	 */
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new AntiRepeatSubmitInterceptor())
			.addPathPatterns("/**")
			.excludePathPatterns(
				// 排除生成token的接口
				"/goods/token/**",
				// 排除静态资源
				"/**/*.html",
				"/**/*.js",
				"/**/*.css",
				"/**/*.ico",
				"/**/*.png",
				"/**/*.jpg",
				"/**/*.gif",
				// 排除Swagger相关
				"/v3/api-docs/**",
				"/swagger-ui/**",
				"/doc.html",
				// 排除健康检查
				"/actuator/**"
			)
			.order(Ordered.HIGHEST_PRECEDENCE + 1);
	}

}
