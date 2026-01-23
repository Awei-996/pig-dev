package cn.com.k12code.pigseckill.goods.service.impl;

import cn.com.k12code.pigseckill.goods.dto.SkGoodsDTO;
import cn.com.k12code.pigseckill.goods.entity.SkGoods;
import cn.com.k12code.pigseckill.goods.enums.GoodsStateEnum;
import cn.com.k12code.pigseckill.goods.mapper.SkGoodsMapper;
import cn.com.k12code.pigseckill.goods.service.SkGoodsService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 商品服务实现类
 *
 * @author carl
 * @date 2025/01/23
 */
@Service
@AllArgsConstructor
public class SkGoodsServiceImpl extends ServiceImpl<SkGoodsMapper, SkGoods> implements SkGoodsService {

	/**
	 * 分页查询商品信息
	 * @param page 分页对象
	 * @param skGoodsDTO 商品查询条件
	 * @return 分页结果
	 */
	@Override
	public IPage<SkGoods> getGoodsPage(Page<SkGoods> page, SkGoodsDTO skGoodsDTO) {
		LambdaQueryWrapper<SkGoods> wrapper = buildQueryWrapper(skGoodsDTO);
		return baseMapper.selectPage(page, wrapper);
	}

	/**
	 * 查询商品列表
	 * @param skGoodsDTO 商品查询条件
	 * @return 商品列表
	 */
	@Override
	public List<SkGoods> listGoods(SkGoodsDTO skGoodsDTO) {
		LambdaQueryWrapper<SkGoods> wrapper = buildQueryWrapper(skGoodsDTO);
		return baseMapper.selectList(wrapper);
	}

	/**
	 * 根据ID查询商品详情
	 * @param id 商品ID
	 * @return 商品信息
	 */
	@Override
	public SkGoods getGoodsById(Long id) {
		return baseMapper.selectById(id);
	}

	/**
	 * 保存商品信息
	 * @param skGoods 商品实体
	 * @return 是否保存成功
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public Boolean saveGoods(SkGoods skGoods) {
		return save(skGoods);
	}

	/**
	 * 更新商品信息
	 * @param skGoods 商品实体
	 * @return 是否更新成功
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public Boolean updateGoods(SkGoods skGoods) {
		// 如果商品被驳回后再次修改，状态自动变为待审核
		if (skGoods.getId() != null) {
			SkGoods existingGoods = baseMapper.selectById(skGoods.getId());
			if (existingGoods != null && GoodsStateEnum.REJECTED.getCode().equals(existingGoods.getState())) {
				skGoods.setState(GoodsStateEnum.PENDING.getCode());
			}
		}
		return updateById(skGoods);
	}

	/**
	 * 根据ID删除商品（逻辑删除）
	 * @param id 商品ID
	 * @return 是否删除成功
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public Boolean removeGoodsById(Long id) {
		return removeById(id);
	}

	/**
	 * 批量删除商品（逻辑删除）
	 * @param ids 商品ID列表
	 * @return 是否删除成功
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public Boolean removeGoodsByIds(List<Long> ids) {
		return removeByIds(ids);
	}

	/**
	 * 审核通过商品
	 * @param id 商品ID
	 * @return 是否审核成功
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public Boolean approveGoods(Long id) {
		SkGoods goods = baseMapper.selectById(id);
		if (goods == null) {
			return false;
		}
		goods.setState(GoodsStateEnum.ON_SALE.getCode());
		return updateById(goods);
	}

	/**
	 * 驳回商品
	 * @param id 商品ID
	 * @return 是否驳回成功
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public Boolean rejectGoods(Long id) {
		SkGoods goods = baseMapper.selectById(id);
		if (goods == null) {
			return false;
		}
		goods.setState(GoodsStateEnum.REJECTED.getCode());
		return updateById(goods);
	}

	/**
	 * 构建查询条件
	 * @param skGoodsDTO 查询条件DTO
	 * @return LambdaQueryWrapper对象
	 */
	private LambdaQueryWrapper<SkGoods> buildQueryWrapper(SkGoodsDTO skGoodsDTO) {
		LambdaQueryWrapper<SkGoods> wrapper = Wrappers.lambdaQuery();

		if (skGoodsDTO == null) {
			return wrapper;
		}

		// 主键ID
		if (skGoodsDTO.getId() != null) {
			wrapper.eq(SkGoods::getId, skGoodsDTO.getId());
		}

		// 商品名称（模糊查询）
		if (skGoodsDTO.getName() != null && !skGoodsDTO.getName().trim().isEmpty()) {
			wrapper.like(SkGoods::getName, skGoodsDTO.getName());
		}

		// 商品类目ID
		if (skGoodsDTO.getClassId() != null && !skGoodsDTO.getClassId().trim().isEmpty()) {
			wrapper.eq(SkGoods::getClassId, skGoodsDTO.getClassId());
		}

		// 状态
		if (skGoodsDTO.getState() != null && !skGoodsDTO.getState().trim().isEmpty()) {
			wrapper.eq(SkGoods::getState, skGoodsDTO.getState());
		}

		// 是否可以预约
		if (skGoodsDTO.getCanBook() != null) {
			wrapper.eq(SkGoods::getCanBook, skGoodsDTO.getCanBook());
		}

		// 价格范围
		if (skGoodsDTO.getMinPrice() != null) {
			wrapper.ge(SkGoods::getPrice, skGoodsDTO.getMinPrice());
		}
		if (skGoodsDTO.getMaxPrice() != null) {
			wrapper.le(SkGoods::getPrice, skGoodsDTO.getMaxPrice());
		}

		// 商品创建时间范围
		if (skGoodsDTO.getCreateTime() != null && skGoodsDTO.getCreateTime().length == 2) {
			if (skGoodsDTO.getCreateTime()[0] != null) {
				wrapper.ge(SkGoods::getCreateTime, skGoodsDTO.getCreateTime()[0]);
			}
			if (skGoodsDTO.getCreateTime()[1] != null) {
				wrapper.le(SkGoods::getCreateTime, skGoodsDTO.getCreateTime()[1]);
			}
		}

		// 商品发售时间范围
		if (skGoodsDTO.getSaleTime() != null && skGoodsDTO.getSaleTime().length == 2) {
			if (skGoodsDTO.getSaleTime()[0] != null) {
				wrapper.ge(SkGoods::getSaleTime, skGoodsDTO.getSaleTime()[0]);
			}
			if (skGoodsDTO.getSaleTime()[1] != null) {
				wrapper.le(SkGoods::getSaleTime, skGoodsDTO.getSaleTime()[1]);
			}
		}

		// 预约开始时间范围
		if (skGoodsDTO.getBookStartTime() != null && skGoodsDTO.getBookStartTime().length == 2) {
			if (skGoodsDTO.getBookStartTime()[0] != null) {
				wrapper.ge(SkGoods::getBookStartTime, skGoodsDTO.getBookStartTime()[0]);
			}
			if (skGoodsDTO.getBookStartTime()[1] != null) {
				wrapper.le(SkGoods::getBookStartTime, skGoodsDTO.getBookStartTime()[1]);
			}
		}

		// 预约结束时间范围
		if (skGoodsDTO.getBookEndTime() != null && skGoodsDTO.getBookEndTime().length == 2) {
			if (skGoodsDTO.getBookEndTime()[0] != null) {
				wrapper.ge(SkGoods::getBookEndTime, skGoodsDTO.getBookEndTime()[0]);
			}
			if (skGoodsDTO.getBookEndTime()[1] != null) {
				wrapper.le(SkGoods::getBookEndTime, skGoodsDTO.getBookEndTime()[1]);
			}
		}

		// 按创建时间倒序
		wrapper.orderByDesc(SkGoods::getGmtCreate);

		return wrapper;
	}

}
