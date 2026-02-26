package cn.com.k12code.pigseckill.order.validator.impl;

import cn.com.k12code.pigseckill.order.request.OrderCreateRequest;
import cn.com.k12code.pigseckill.order.validator.BaseOrderCreateValidator;
import com.pig4cloud.pig.admin.api.dto.UserDTO;
import com.pig4cloud.pig.admin.api.dto.UserInfo;
import com.pig4cloud.pig.admin.api.feign.RemoteUserService;
import com.pig4cloud.pig.common.core.util.R;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 *  用户校验器
 * @author quxw
 */
@AllArgsConstructor
@NoArgsConstructor
public class UserValidator extends BaseOrderCreateValidator {

	private  RemoteUserService remoteUserService;

	@Override
	protected void doValidate(OrderCreateRequest request) throws Exception {
		UserDTO userDTO = new UserDTO();
		userDTO.setUserId(request.getBuyerId());

		R<UserInfo> info = remoteUserService.info(userDTO);

		if (info.getData() != null) {
			UserInfo data = info.getData();
			if ("1".equals(data.getDelFlag())) {
				throw new Exception("当前用户不存在");
			}
			if ("9".equals(data.getLockFlag())) {
				throw new Exception("当前用户已锁定");
			}
		} else {
			throw new Exception("当前用户不存在");
		}
	}
}
