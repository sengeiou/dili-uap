package com.dili.uap.api;

import com.dili.ss.domain.BaseOutput;
import com.dili.ss.dto.DTOUtils;
import com.dili.uap.domain.dto.RoleDto;
import com.dili.uap.sdk.domain.Role;
import com.dili.uap.sdk.domain.dto.RoleUserDto;
import com.dili.uap.service.RoleService;
import io.swagger.annotations.Api;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色Api
 */
@Api("/roleApi")
@RestController
@RequestMapping("/roleApi")
public class RoleApi {
	@Autowired
	RoleService roleService;

	/**
	 * 根据用户id或用户名查询角色
	 * 
	 * @param userId
	 * @param userName
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/listByUser.api", method = { RequestMethod.GET, RequestMethod.POST })
	public BaseOutput<List<Role>> listByUser(@RequestParam(required = false) Long userId, @RequestParam(required = false) String userName) throws Exception {
		if (userId != null) {
			return BaseOutput.success().setData(roleService.listByUserId(userId));
		} else if (StringUtils.isNotBlank(userName)) {
			return BaseOutput.success().setData(roleService.listByUserName(userName));
		}
		return BaseOutput.failure("参数为空");
	}

	/**
	 * 根据角色id
	 * @param roleIds
	 * @return
	 */
	@PostMapping("/listRoleUserByRoleIds.api")
	public BaseOutput<List<RoleUserDto>> listRoleUserByRoleIds(@RequestBody List<Long> roleIds) {
		List<RoleUserDto> list = this.roleService.listRoleUserByRoleIds(roleIds);
		return BaseOutput.success().setData(list);
	}

	/**
	 * 根据ids查询角色列表
	 *
	 * @param ids
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/listRoleByIds.api", method = { RequestMethod.GET, RequestMethod.POST })
	public BaseOutput<List<Role>> listRoleByIds(@RequestBody List<String> ids) {
		RoleDto role = DTOUtils.newInstance(RoleDto.class);
		role.setIds(ids);
		return BaseOutput.success().setData(roleService.listByExample(role));
	}
}