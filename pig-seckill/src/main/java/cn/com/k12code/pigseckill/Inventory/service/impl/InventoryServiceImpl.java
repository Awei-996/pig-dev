package cn.com.k12code.pigseckill.Inventory.service.impl;

import cn.com.k12code.pigseckill.Inventory.dto.InventoryDTO;
import cn.com.k12code.pigseckill.Inventory.service.InventoryService;
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

	@Override
	public R<?> init(InventoryDTO inventoryDTO) {
		// 判断当前商品的库存初始化了吗，如果初始化不能重复执行
		boolean ifAbsent = redissonClient.getBucket(INVENTORY_KEY + inventoryDTO.getGoodsId()).setIfAbsent(inventoryDTO.getInventory());
		return ifAbsent ? R.ok("商品初始化成功") : R.failed("该商品已经初始化");
	}
}
