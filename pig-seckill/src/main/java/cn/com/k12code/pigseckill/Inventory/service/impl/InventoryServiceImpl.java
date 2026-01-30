package cn.com.k12code.pigseckill.Inventory.service.impl;

import cn.com.k12code.pigseckill.Inventory.dto.InventoryDTO;
import cn.com.k12code.pigseckill.Inventory.service.InventoryService;
import cn.com.k12code.pigseckill.Inventory.support.InventoryLuaScriptRunner;
import cn.com.k12code.pigseckill.Inventory.support.InventoryScriptException;
import com.pig4cloud.pig.common.core.util.R;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import static com.pig4cloud.pig.common.core.constant.CacheConstants.INVENTORY_KEY;

/**
 * @author quxw
 */
@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

	private final RedissonClient redissonClient;

	private final InventoryLuaScriptRunner inventoryLuaScriptRunner;

	@Override
	public R<?> init(InventoryDTO inventoryDTO) {
		// 判断当前商品的库存初始化了吗，如果初始化不能重复执行
		boolean ifAbsent = redissonClient.getBucket(INVENTORY_KEY + inventoryDTO.getGoodsId()).setIfAbsent(inventoryDTO.getInventory());
		return ifAbsent ? R.ok("商品初始化成功") : R.failed("该商品已经初始化");
	}

	@Override
	public R<?> decreaseInventory(InventoryDTO inventoryDTO) {
		if (inventoryDTO.getInventory() == null || inventoryDTO.getInventory() <= 0) {
			return R.failed("扣减数量必须大于0");
		}
		if (inventoryDTO.getIdentifier() == null || inventoryDTO.getIdentifier().isBlank()) {
			return R.failed("幂等不能为空（用于防重）");
		}
		try {
			Long remain = inventoryLuaScriptRunner.executeDecrease(
					inventoryDTO.getGoodsId(),
					inventoryDTO.getInventory(),
					inventoryDTO.getIdentifier()
			);
			return R.ok(remain, "扣减成功");
		} catch (InventoryScriptException e) {
			String msg = switch (e.getReason()) {
				case OPERATION_ALREADY_EXECUTED -> "该订单已扣减过库存，请勿重复操作";
				case KEY_NOT_FOUND -> "商品库存未初始化";
				case INVENTORY_IS_ZERO -> "库存已售罄";
				case INVENTORY_NOT_ENOUGH -> "库存不足";
				case INVALID_VALUE -> "库存数据异常";
				default -> e.getMessage();
			};
			return R.failed(msg);
		}
	}

	@Override
	public R<?> increaseInventory(InventoryDTO inventoryDTO) {
		if (inventoryDTO.getInventory() == null || inventoryDTO.getInventory() <= 0) {
			return R.failed("增加数量必须大于0");
		}
		if (inventoryDTO.getIdentifier() == null || inventoryDTO.getIdentifier().isBlank()) {
			return R.failed("幂等不能为空（用于防重）");
		}
		try {
			Long remain = inventoryLuaScriptRunner.executeIncrease(
					inventoryDTO.getGoodsId(),
					inventoryDTO.getInventory(),
					inventoryDTO.getIdentifier()
			);
			return R.ok(remain, "增加成功");
		} catch (InventoryScriptException e) {
			String msg = switch (e.getReason()) {
				case OPERATION_ALREADY_EXECUTED -> "该操作已执行过，请勿重复增加";
				case KEY_NOT_FOUND -> "商品库存未初始化";
				case INVALID_VALUE -> "库存数据异常";
				default -> e.getMessage();
			};
			return R.failed(msg);
		}
	}
}
