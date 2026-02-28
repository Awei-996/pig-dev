package cn.com.k12code.pigseckill.goods.service;

import cn.com.k12code.pigseckill.goods.dto.SkGoodsDTO;
import cn.com.k12code.pigseckill.goods.entity.SkGoods;
import cn.com.k12code.pigseckill.order.request.OrderCreateAndConfirmRequest;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.pig4cloud.pig.common.core.util.R;

import java.util.List;

/**
 * 商品服务接口
 *
 * @author carl
 * @date 2025/01/23
 */
public interface SkGoodsService extends IService<SkGoods> {

	/**
	 * 分页查询商品信息
	 * @param page 分页对象
	 * @param skGoodsDTO 商品查询条件
	 * @return 分页结果
	 */
	IPage<SkGoods> getGoodsPage(Page<SkGoods> page, SkGoodsDTO skGoodsDTO);

	/**
	 * 查询商品列表
	 * @param skGoodsDTO 商品查询条件
	 * @return 商品列表
	 */
	List<SkGoods> listGoods(SkGoodsDTO skGoodsDTO);

	/**
	 * 根据ID查询商品详情
	 * @param id 商品ID
	 * @return 商品信息
	 */
	SkGoods getGoodsById(String id);

	/**
	 * 保存商品信息
	 * @param skGoods 商品实体
	 * @return 是否保存成功
	 */
	Boolean saveGoods(SkGoods skGoods);

	/**
	 * 更新商品信息
	 * @param skGoods 商品实体
	 * @return 是否更新成功
	 */
	Boolean updateGoods(SkGoods skGoods);

	/**
	 * 根据ID删除商品（逻辑删除）
	 * @param id 商品ID
	 * @return 是否删除成功
	 */
	Boolean removeGoodsById(String id);

	/**
	 * 批量删除商品（逻辑删除）
	 * @param ids 商品ID列表
	 * @return 是否删除成功
	 */
	Boolean removeGoodsByIds(List<String> ids);

	/**
	 * 审核通过商品
	 * @param id 商品ID
	 * @return 是否审核成功
	 */
	R<?> approveGoods(String id);

	/**
	 * 驳回商品
	 * @param id 商品ID
	 * @return 是否驳回成功
	 */
	Boolean rejectGoods(String id);

	/**
	 * 购买商品
	 * @param orderCreateAndConfirmRequest 购买信息
	 * @return 返回是否购买陈功
	 */
	R<?> buyGood(OrderCreateAndConfirmRequest orderCreateAndConfirmRequest);
}
