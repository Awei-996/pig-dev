package cn.com.k12code.pigseckill.Inventory.service;

import cn.com.k12code.pigseckill.Inventory.dto.InventoryDTO;
import com.pig4cloud.pig.common.core.util.R;

/**
 * @author quxw
 */
public interface InventoryService {

	R<?> init(InventoryDTO inventoryDTO);

	R<?> decreaseInventory(InventoryDTO inventoryDTO);

	R<?> increaseInventory(InventoryDTO inventoryDTO);
}
