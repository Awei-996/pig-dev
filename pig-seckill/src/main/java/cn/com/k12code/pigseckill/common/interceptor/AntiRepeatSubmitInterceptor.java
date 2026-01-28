package cn.com.k12code.pigseckill.common.interceptor;

import cn.com.k12code.pigseckill.common.annotation.AntiRepeatSubmit;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.pig4cloud.pig.common.core.util.R;
import com.pig4cloud.pig.common.core.util.RedisUtils;
import com.pig4cloud.pig.common.security.service.PigUser;
import com.pig4cloud.pig.common.security.util.SecurityUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.MediaType;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

import static com.pig4cloud.pig.common.core.constant.CacheConstants.*;

/**
 * 防重复提交拦截器
 * <p>
 * 用于验证防重复提交token，确保接口只能被调用一次
 * </p>
 *
 * @author quxw
 * @date 2025/01/26
 */
@Slf4j
public class AntiRepeatSubmitInterceptor implements HandlerInterceptor {

	public static final ThreadLocal<String> TOKEN_THREAD_LOCAL = new ThreadLocal<>();


	@Override
	public boolean preHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) throws Exception {
		try {
			// 非控制器方法直接放行
			if (!(handler instanceof HandlerMethod handlerMethod)) {
				return true;
			}

			AntiRepeatSubmit annotation = AnnotationUtils.getAnnotation(handlerMethod.getMethod(), AntiRepeatSubmit.class);

			// 没有注解直接放行
			if (annotation == null) {
				return true;
			}

			// 验证用户是否登录
			PigUser user = SecurityUtils.getUser();
			if (user == null) {
				writeErrorResponse(response, R.failed("用户未登录"));
				return false;
			}

			// 获取token
			String token = getToken(request, annotation);
			if (StrUtil.isBlank(token)) {
				writeErrorResponse(response, R.failed("防重复提交token不能为空"));
				return false;
			}

			// 解密
			String decryptValue = SecurityUtils.decrypt(token, ENCRYPT_KEY);
			String tokenKey = decryptValue.substring(0, decryptValue.lastIndexOf(CACHE_VALUE_HYPHEN));

			// 从Redis获取token
			String storedToken = RedisUtils.get(tokenKey);
			if (StrUtil.isBlank(storedToken)) {
				log.warn("防重复提交token验证失败：token不存在或已使用，key={}", tokenKey);
				writeErrorResponse(response, R.failed("请勿重复提交或token已过期"));
				return false;
			}

			// 验证token是否匹配
			if (!token.equals(storedToken)) {
				log.warn("防重复提交token验证失败：token不匹配，key={}", tokenKey);
				writeErrorResponse(response, R.failed("防重复提交token验证失败"));
				return false;
			}

			// 验证成功，删除token（确保一次性使用）
			String string = RedisUtils.getAndDelete(tokenKey).toString();
			// 存入线程中
			TOKEN_THREAD_LOCAL.set(string);
			log.debug("防重复提交token验证成功并已删除，key={}", tokenKey);

			return true;
		} finally {
			TOKEN_THREAD_LOCAL.remove();
		}
	}

	/**
	 * 获取token
	 * 优先从请求头获取，如果请求头中没有，则从请求参数获取
	 */
	private String getToken(HttpServletRequest request, AntiRepeatSubmit annotation) {
		// 先从请求头获取
		String token = request.getHeader(annotation.tokenParam());
		if (StrUtil.isNotBlank(token)) {
			return token;
		}

		// 如果请求头中没有，从请求参数获取
		token = request.getParameter(annotation.tokenParam());
		return token;
	}

	/**
	 * 写入错误响应
	 */
	private void writeErrorResponse(HttpServletResponse response, R<?> result) throws Exception {
		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setCharacterEncoding(StandardCharsets.UTF_8.name());

		try (PrintWriter writer = response.getWriter()) {
			writer.write(JSONUtil.toJsonStr(result));
			writer.flush();
		}
	}

}
