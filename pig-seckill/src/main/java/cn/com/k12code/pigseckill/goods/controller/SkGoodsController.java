package cn.com.k12code.pigseckill.goods.controller;

import cn.com.k12code.pigseckill.goods.dto.BuyDTO;
import cn.com.k12code.pigseckill.goods.dto.SkGoodsDTO;
import cn.com.k12code.pigseckill.goods.entity.SkGoods;
import cn.com.k12code.pigseckill.goods.service.SkGoodsService;
import cn.com.k12code.pigseckill.order.request.OrderCreateAndConfirmRequest;
import cn.com.k12code.pigseckill.order.validator.OrderCreateValidator;
import cn.com.k12code.pigseckill.utils.SnowflakeIdGenerator;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pig4cloud.pig.common.core.util.R;
import com.pig4cloud.pig.common.security.util.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static cn.com.k12code.pigseckill.common.interceptor.AntiRepeatSubmitInterceptor.TOKEN_THREAD_LOCAL;

/**
 * 商品管理控制器
 *
 * @author carl
 * @date 2025/01/23
 */
@RestController
@RequestMapping("/goods")
@Tag(description = "goods", name = "商品管理模块")
@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
@RequiredArgsConstructor
public class SkGoodsController {

	private final SkGoodsService skGoodsService;

	/**
	 * 通过ID查询商品信息
	 * @param id 商品ID
	 * @return 包含商品信息的响应对象
	 */
	@GetMapping("/{id}")
	@Operation(summary = "通过ID查询商品信息", description = "通过ID查询商品信息")
	public R<SkGoods> getById(@PathVariable String id) {
		return R.ok(skGoodsService.getGoodsById(id));
	}

	/**
	 * 分页查询商品信息
	 * @param page 分页对象
	 * @param skGoodsDTO 商品查询条件
	 * @return 包含分页结果的响应对象
	 */
	@GetMapping("/page")
	@Operation(summary = "分页查询商品信息", description = "分页查询商品信息")
	public R<IPage<SkGoods>> getGoodsPage(@ParameterObject Page<SkGoods> page,
			@ParameterObject SkGoodsDTO skGoodsDTO) {
		return R.ok(skGoodsService.getGoodsPage(page, skGoodsDTO));
	}

	/**
	 * 查询商品列表
	 * @param skGoodsDTO 商品查询条件
	 * @return 包含商品列表的响应结果
	 */
	@GetMapping("/list")
	@Operation(summary = "查询商品列表", description = "查询商品列表")
	public R<List<SkGoods>> listGoods(@ParameterObject SkGoodsDTO skGoodsDTO) {
		return R.ok(skGoodsService.listGoods(skGoodsDTO));
	}

	/**
	 * 保存商品信息
	 * @param skGoods 商品实体
	 * @return 操作结果
	 */
	@PostMapping
	@Operation(summary = "保存商品信息", description = "保存商品信息")
	public R<Boolean> saveGoods(@Valid @RequestBody SkGoods skGoods) {
		return R.ok(skGoodsService.saveGoods(skGoods));
	}

	/**
	 * 更新商品信息
	 * @param skGoods 商品实体
	 * @return 操作结果
	 */
	@PutMapping
	@Operation(summary = "更新商品信息", description = "更新商品信息")
	public R<Boolean> updateGoods(@Valid @RequestBody SkGoods skGoods) {
		return R.ok(skGoodsService.updateGoods(skGoods));
	}

	/**
	 * 根据ID删除商品（逻辑删除）
	 * @param id 商品ID
	 * @return 操作结果
	 */
	@DeleteMapping("/{id}")
	@Operation(summary = "根据ID删除商品", description = "根据ID删除商品（逻辑删除）")
	public R<Boolean> removeById(@PathVariable String id) {
		return R.ok(skGoodsService.removeGoodsById(id));
	}

	/**
	 * 批量删除商品（逻辑删除）
	 * @param ids 商品ID列表
	 * @return 操作结果
	 */
	@DeleteMapping("/batch")
	@Operation(summary = "批量删除商品", description = "批量删除商品（逻辑删除）")
	public R<Boolean> removeByIds(@RequestBody List<String> ids) {
		return R.ok(skGoodsService.removeGoodsByIds(ids));
	}

	/**
	 * 审核通过商品
	 * @param id 商品ID
	 * @return 操作结果
	 */
	@PutMapping("/{id}/approve")
	@Operation(summary = "审核通过商品", description = "审核通过商品，状态变为上架")
	public R<?> approveGoods(@PathVariable String id) {
		return skGoodsService.approveGoods(id);
	}

	/**
	 * 驳回商品
	 * @param id 商品ID
	 * @return 操作结果
	 */
	@PutMapping("/{id}/reject")
	@Operation(summary = "驳回商品", description = "驳回商品，状态变为驳回")
	public R<Boolean> rejectGoods(@PathVariable String id) {
		return R.ok(skGoodsService.rejectGoods(id));
	}
	//-------------------------下单处理

	@Resource
	private  OrderCreateValidator orderCreateChain;

	/**
	 * 秒杀下单
	 */
	@PostMapping("/buy")
	public R<?> buy(@Valid @RequestBody BuyDTO buyDTO){
		try {
			OrderCreateAndConfirmRequest orderCreateAndConfirmRequest = getOrderCreateAndConfirmRequest(buyDTO);
			orderCreateChain.validate(orderCreateAndConfirmRequest);
		} catch (Exception e) {
			return R.failed(e.getMessage());
		}
		return R.ok("");
	}

	private OrderCreateAndConfirmRequest getOrderCreateAndConfirmRequest(BuyDTO buyDTO) {
		// 创建订单号
		String orderId = SnowflakeIdGenerator.nextId(buyDTO.getGoodsType());
		// 封装创建和确定订单
		OrderCreateAndConfirmRequest orderCreateAndConfirmRequest = new OrderCreateAndConfirmRequest();
		orderCreateAndConfirmRequest.setGoodsId(buyDTO.getGoodsId());
		orderCreateAndConfirmRequest.setOrderId(orderId);
		orderCreateAndConfirmRequest.setBuyerId(SecurityUtils.getUser().getId());
		orderCreateAndConfirmRequest.setOperateTime(LocalDateTime.now());
		orderCreateAndConfirmRequest.setItemCount(buyDTO.getItemCount());
		orderCreateAndConfirmRequest.setItemPrice(buyDTO.getItemPrice());
		orderCreateAndConfirmRequest.setOrderAmount(orderCreateAndConfirmRequest.getItemPrice().multiply(new BigDecimal(orderCreateAndConfirmRequest.getItemCount())));
		orderCreateAndConfirmRequest.setIdentifier(TOKEN_THREAD_LOCAL.get());
		return orderCreateAndConfirmRequest;
	}

	}
