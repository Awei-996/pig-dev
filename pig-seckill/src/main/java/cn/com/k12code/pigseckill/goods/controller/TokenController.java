package cn.com.k12code.pigseckill.goods.controller;

import cn.com.k12code.pigseckill.goods.entity.SkGoods;
import cn.com.k12code.pigseckill.goods.enums.GoodsStateEnum;
import cn.com.k12code.pigseckill.goods.enums.GoodsTypeEnum;
import cn.com.k12code.pigseckill.goods.service.SkGoodsService;
import com.pig4cloud.pig.common.core.util.R;
import com.pig4cloud.pig.common.core.util.RedisUtils;
import com.pig4cloud.pig.common.security.util.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.pig4cloud.pig.common.core.constant.CacheConstants.*;

/**
 * 防重提交Token控制器
 *
 * @author quxw
 * @date 2025/01/26
 */
@RestController
@AllArgsConstructor
@RequestMapping("/goods/token")
@Tag(description = "token", name = "商品防重提交Token")
@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
public class TokenController {

	private final SkGoodsService skGoodsService;

	/**
	 * 生成防重提交token
	 * @param scene 场景标识（如：茅台、nft等）
	 * @param key 业务唯一标识（如：商品ID等）
	 * @return token
	 */
	@GetMapping("/get")
	@Operation(summary = "获取防重提交token", description = "生成防重提交token，用于防止重复提交")
	public R<String> getToken(
			@NotBlank(message = "场景标识不能为空") @RequestParam String scene,
			@NotBlank(message = "业务标识不能为空") @RequestParam String key) {
		try {
			// 判断是否属于定义的商品类型防止假商品刷token
			Arrays.stream(GoodsTypeEnum.values())
					.filter(goodsTypeEnum -> goodsTypeEnum.name().equals(scene))
					.findFirst()
					.orElseThrow(() -> new RuntimeException("当前商品类型不存在"));

			// 再次验证已经登录
			if (SecurityUtils.getAuthentication().isAuthenticated()) {
				// 查询商品
				SkGoods skGoods = skGoodsService.getGoodsById(key);
				if (skGoods == null || !skGoods.getState().equals(GoodsStateEnum.ON_SALE.getCode())) {
					throw new RuntimeException("当前商品不可用");
				}
				// 组成key token:nft:29
				Long userId = SecurityUtils.getUser().getId();
				String tokenKey = TOKEN_PREFIX + scene + CACHE_KEY_SEPARATOR + key + CACHE_KEY_SEPARATOR + userId;
				// value
				String uuid = UUID.randomUUID().toString().replace("-", "");
				// 同时把key存到value, 因为后续拦截器用的到
				String tokenValue = tokenKey + CACHE_VALUE_HYPHEN + uuid;
				String value = SecurityUtils.encrypt(tokenValue, ENCRYPT_KEY);
				// 设置缓存
				RedisUtils.set(tokenKey, value, 30, TimeUnit.MINUTES);

				return R.ok(value);
			}
			throw new RuntimeException("用户未登录");
		} catch (Exception e) {
			return R.failed("生成token失败：" + e.getMessage());
		}
	}

}
