package com.dili.uap.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.dili.ss.base.BaseServiceImpl;
import com.dili.ss.constant.ResultCode;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.domain.EasyuiPageOutput;
import com.dili.ss.dto.DTO;
import com.dili.ss.dto.DTOUtils;
import com.dili.ss.metadata.ValueProviderUtils;
import com.dili.ss.util.AESUtils;
import com.dili.ss.util.POJOUtils;
import com.dili.uap.boot.RabbitConfiguration;
import com.dili.uap.constants.UapConstants;
import com.dili.uap.dao.DepartmentMapper;
import com.dili.uap.dao.FirmMapper;
import com.dili.uap.dao.RoleMapper;
import com.dili.uap.dao.UserDataAuthMapper;
import com.dili.uap.dao.UserMapper;
import com.dili.uap.dao.UserRoleMapper;
import com.dili.uap.domain.UserRole;
import com.dili.uap.domain.dto.UserDataDto;
import com.dili.uap.domain.dto.UserDepartmentRole;
import com.dili.uap.domain.dto.UserDepartmentRoleQuery;
import com.dili.uap.domain.dto.UserDto;
import com.dili.uap.glossary.UserState;
import com.dili.uap.manager.UserManager;
import com.dili.uap.rpc.ProjectRpc;
import com.dili.uap.sdk.domain.Department;
import com.dili.uap.sdk.domain.Firm;
import com.dili.uap.sdk.domain.Role;
import com.dili.uap.sdk.domain.User;
import com.dili.uap.sdk.domain.UserDataAuth;
import com.dili.uap.sdk.domain.UserTicket;
import com.dili.uap.sdk.glossary.DataAuthType;
import com.dili.uap.sdk.session.SessionContext;
import com.dili.uap.service.DataAuthRefService;
import com.dili.uap.service.LoginService;
import com.dili.uap.service.UserService;
import com.dili.uap.utils.MD5Util;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * ???MyBatis Generator?????????????????? This file was generated on 2018-05-18 10:46:46.
 */
@Service
public class UserServiceImpl extends BaseServiceImpl<User, Long> implements UserService {

	@Autowired
	private MD5Util md5Util;

	@Autowired
	private UserManager userManager;
	@Autowired
	private ProjectRpc projectRpc;

	public UserMapper getActualDao() {
		return (UserMapper) getDao();
	}

	@Value("${uap.adminName:admin}")
	private String adminName;
	@Autowired
	RoleMapper roleMapper;
	@Autowired
	UserRoleMapper userRoleMapper;
	@Autowired
	FirmMapper firmMapper;
	@Autowired
	DepartmentMapper departmentMapper;
	@Autowired
	UserDataAuthMapper userDataAuthMapper;
	@Autowired
	private AmqpTemplate amqpTemplate;
	@Value("${aesKey:}")
	private String aesKey;
	@Autowired
	DataAuthRefService dataAuthRefService;
	@Autowired
	private LoginService loginService;

	public static final String ALM_PROJECT_PREFIX = "alm_";

	@Override
	public void logout(String sessionId) {
		this.userManager.clearSession(sessionId);
	}

	@Override
	public List<User> findUserByRole(Long roleId) {
		return getActualDao().findUserByRole(roleId);
	}

	@Transactional
	@Override
	public BaseOutput<Object> changePwd(Long userId, UserDto user) {
		if (userId == null) {
			return BaseOutput.failure("???????????????");
		}
		if (StringUtils.isBlank(user.getOldPassword())) {
			return BaseOutput.failure("????????????????????????");
		}
		if (StringUtils.isBlank(user.getNewPassword())) {
			return BaseOutput.failure("?????????????????????");
		}
		if (user.getNewPassword().trim().length() < 6) {
			return BaseOutput.failure("????????????????????????6-20");
		}
		if (user.getNewPassword().trim().length() > 20) {
			return BaseOutput.failure("????????????????????????6-20");
		}

		if (user.getNewPassword().equals(user.getOldPassword())) {
			return BaseOutput.failure("????????????????????????????????????");
		}
		if (!user.getNewPassword().equals(user.getConfirmPassword())) {
			return BaseOutput.failure("???????????????????????????,???????????????");
		}

		User userInDB = this.get(userId);
		if (userInDB == null) {
			return BaseOutput.failure("??????????????????");
		}
		// ?????????????????????
		if (!StringUtils.equalsIgnoreCase(userInDB.getPassword(), this.encryptPwd(user.getOldPassword()))) {
			return BaseOutput.failure("??????????????????,???????????????");
		}

		userInDB.setModified(new Date());
		// ????????????
		userInDB.setState(UserState.NORMAL.getCode());
		// ?????????????????????
		userInDB.setPassword(this.encryptPwd(user.getNewPassword()));
		this.updateExactSimple(userInDB);
		// ????????????????????????
		user.setPassword(this.encryptPwd(user.getNewPassword()));
		user.setUserName(userInDB.getUserName());
		String json = JSON.toJSONString(user);
		json = AESUtils.encrypt(json, aesKey);
		amqpTemplate.convertAndSend(RabbitConfiguration.UAP_TOPIC_EXCHANGE, RabbitConfiguration.UAP_CHANGE_PASSWORD_KEY, json);
		return BaseOutput.success("??????????????????");
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public BaseOutput save(User user) {
		if (StringUtils.isNotBlank(user.getUserName())) {
			user.setUserName(user.getUserName().toLowerCase());
		}
		// ????????????????????????
		User query = DTOUtils.newInstance(User.class);
		query.setEmail(user.getEmail());
		List<User> userList = getActualDao().select(query);
		// ????????????
		if (null == user.getId()) {
			if (CollectionUtils.isNotEmpty(userList)) {
				return BaseOutput.failure("???????????????");
			}
			query.setEmail(null);
			query.setCellphone(user.getCellphone());
			userList = getActualDao().select(query);
			if (CollectionUtils.isNotEmpty(userList)) {
				return BaseOutput.failure("?????????????????????");
			}
			query.setCellphone(null);
			query.setUserName(user.getUserName());
			userList = getActualDao().select(query);
			if (CollectionUtils.isNotEmpty(userList)) {
				return BaseOutput.failure("?????????????????????");
			} else {
				user.setState(UserState.INACTIVE.getCode());
			}
			user.setPassword(encryptPwd(UapConstants.DEFAULT_PASS));
			this.insertExactSimple(user);
			User newUser = DTOUtils.newInstance(User.class);
			newUser.setUserName(user.getUserName());
			newUser.setPassword(user.getPassword());
			newUser.setRealName(user.getRealName());
			newUser.setEmail(user.getEmail());
			newUser.setSerialNumber(user.getSerialNumber());
			newUser.setCellphone(user.getCellphone());
			String json = JSON.toJSONString(newUser);
			json = AESUtils.encrypt(json, aesKey);
			amqpTemplate.convertAndSend(RabbitConfiguration.UAP_TOPIC_EXCHANGE, RabbitConfiguration.UAP_ADD_USER_KEY, json);
		} else {
			if (CollectionUtils.isNotEmpty(userList)) {
				// ?????????????????????ID?????????????????????????????????
				boolean result = userList.stream().anyMatch(u -> !u.getId().equals(user.getId()));
				if (result) {
					return BaseOutput.failure("???????????????");
				}
			}
			query.setEmail(null);
			query.setCellphone(user.getCellphone());
			userList = getActualDao().select(query);
			if (CollectionUtils.isNotEmpty(userList)) {
				// ?????????????????????ID?????????????????????????????????
				boolean result = userList.stream().anyMatch(u -> !u.getId().equals(user.getId()));
				if (result) {
					return BaseOutput.failure("?????????????????????");
				}
			}
			User update = DTOUtils.asInstance(user, User.class);
			DTO go = DTOUtils.go(update);
			go.remove("userName");
			go.remove("password");
			go.remove("firmCode");
			go.remove("created");
			go.remove("modified");
			this.updateExactSimple(update);
		}
		return BaseOutput.success("????????????");
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public BaseOutput resetPass(Long userId) {
		User user = DTOUtils.newInstance(User.class);
		user.setId(userId);
		user.setPassword(encryptPwd(UapConstants.DEFAULT_PASS));
		user.setState(UserState.INACTIVE.getCode());
		this.updateSelective(user);
		return BaseOutput.success("????????????");
	}

	@Override
	public BaseOutput upateEnable(Long userId, Boolean enable) {
		User user = DTOUtils.newInstance(User.class);
		user.setId(userId);
		if (enable) {
			user.setState(UserState.NORMAL.getCode());
		} else {
			user.setState(UserState.DISABLED.getCode());
		}
		this.updateSelective(user);
		return BaseOutput.success("????????????");
	}

	@Override
	public List<UserDataDto> getUserRolesForTree(Long userId) {
		// ???????????????????????????????????????
		User user = this.get(userId);
		// ???????????????????????????????????????????????????
		Boolean isGroup = false;
		if (UapConstants.GROUP_CODE.equalsIgnoreCase(user.getFirmCode())) {
			isGroup = true;
		}
		// ??????????????????????????????
		List<Firm> firmList = null;
		// ??????????????????????????????
		List<Role> roleList = null;
		if (isGroup) {
			roleList = roleMapper.selectAll();
			firmList = firmMapper.selectAll();
		} else {
			// ?????????????????????????????????????????????????????????
			Role roleQuery = DTOUtils.newInstance(Role.class);
			roleQuery.setFirmCode(user.getFirmCode());
			roleList = roleMapper.select(roleQuery);
			// ???????????????????????????
			Firm firmQuery = DTOUtils.newInstance(Firm.class);
			firmQuery.setCode(user.getFirmCode());
			firmList = firmMapper.select(firmQuery);
		}
		// ???????????????????????????????????????????????????????????????????????????????????????
		if (CollectionUtils.isNotEmpty(roleList)) {
			Set<Long> userRoleIds = userRoleMapper.getRoleIdsByUserId(user.getId());
			List<UserDataDto> userRoleDtos = Lists.newArrayList();
			/**
			 * ??????????????????????????????????????? ????????????????????????open??????
			 */
			roleList.stream().forEach(role -> {
				UserDataDto dto = DTOUtils.newInstance(UserDataDto.class);
				dto.setName(role.getRoleName());
				dto.setParentId(UapConstants.FIRM_PREFIX + role.getFirmCode());
				dto.setTreeId(String.valueOf(role.getId()));
				if (userRoleIds.contains(role.getId())) {
					dto.setChecked(true);
				} else {
					dto.setChecked(false);
				}
				userRoleDtos.add(dto);
			});
			firmList.stream().forEach(firm -> {
				UserDataDto dto = DTOUtils.newInstance(UserDataDto.class);
				dto.setName(firm.getName());
				dto.setTreeId(UapConstants.FIRM_PREFIX + firm.getCode());
				dto.setParentId("");
				userRoleDtos.add(dto);
			});
			return userRoleDtos;
		}
		return null;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public BaseOutput saveUserRoles(Long userId, String[] roleIds) {
		if (null == userId) {
			return BaseOutput.failure("??????????????????");
		}
		UserRole userRole = DTOUtils.newInstance(UserRole.class);
		userRole.setUserId(userId);
		userRoleMapper.delete(userRole);
		// ?????????????????????????????????
		if (null != roleIds && roleIds.length > 0) {
			List<UserRole> saveDatas = Lists.newArrayList();
			for (String id : roleIds) {
				if (!id.startsWith(UapConstants.FIRM_PREFIX)) {
					UserRole ur = DTOUtils.newInstance(UserRole.class);
					ur.setUserId(userId);
					ur.setRoleId(Long.valueOf(id));
					saveDatas.add(ur);
				}
			}
			// ???????????????????????????????????????????????????????????????
			if (CollectionUtils.isNotEmpty(saveDatas)) {
				userRoleMapper.insertList(saveDatas);
			}
		}
		return BaseOutput.success("????????????");
	}

	@Override
	public EasyuiPageOutput selectForEasyuiPage(UserDto domain, boolean useProvider) throws Exception {
		if (domain.getRows() != null && domain.getRows() >= 1) {
			PageHelper.startPage(domain.getPage(), domain.getRows());
		}
		if (StringUtils.isNotBlank(domain.getSort())) {
			domain.setSort(POJOUtils.humpToLineFast(domain.getSort()));
		}
		String firmCode = SessionContext.getSessionContext().getUserTicket().getFirmCode();
		if (!UapConstants.GROUP_CODE.equals(firmCode)) {
			domain.setFirmCode(firmCode);
		}
		List<UserDto> users = getActualDao().selectForPage(domain);
		long total = users instanceof Page ? ((Page) users).getTotal() : (long) users.size();
		List results = useProvider ? ValueProviderUtils.buildDataByProvider(domain, users) : users;
		return new EasyuiPageOutput((int) total, results);
	}

	@Override
	public BaseOutput<Object> fetchLoginUserInfo(Long userId) {
		User user = this.get(userId);
		if (user == null) {
			return BaseOutput.success("????????????");
		}
		user.setPassword("");
		Map<String, Object> map = DTOUtils.go(user);
		Department department = departmentMapper.selectByPrimaryKey(user.getDepartmentId());
		if (department != null) {
			map.put("departmentId", department.getName());
		}

		if (StringUtils.isNotBlank(user.getFirmCode())) {
			Firm firmConditon = DTOUtils.newInstance(Firm.class);
			firmConditon.setCode(user.getFirmCode());
			Firm firm = this.firmMapper.selectOne(firmConditon);
			if (firm != null) {
				map.put("firmCode", firm.getName());
			}
		}
		return BaseOutput.success("????????????").setData(map);
	}

	@Override
	public List<UserDataDto> getUserDataAuthForTree(Long userId) {
		// ?????????????????????????????????????????????
		User user = this.get(userId);
		if (null == user) {
			return null;
		}
		UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
		if (userTicket == null) {
			return null;
		}
		Map<String, Object> params = Maps.newHashMap();
		params.put("userId", userId);
		// ===================================================
		// ??????UAP????????????????????????????????????????????????????????????????????????????????????admin???????????????????????????????????????
		// ?????????????????????????????????????????????????????????
		// ===================================================
		if (!userTicket.getUserName().equalsIgnoreCase(adminName)) {
			params.put("loginUserId", userTicket.getId());
		}
		return getActualDao().selectUserDatas(params);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public BaseOutput<List<UserDataAuth>> saveUserDatas(Long userId, String[] dataIds, Long dataRange) {
		if (null == userId || null == dataRange) {
			return BaseOutput.failure("??????????????????");
		}
		UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
		if (userTicket == null) {
			return BaseOutput.failure("???????????????");
		}
		List<UserDataAuth> saveDatas = this.convertToSaveUserDatas(userId, dataRange, dataIds);
		UserDataAuth record = DTOUtils.newInstance(UserDataAuth.class);
		record.setUserId(userId);
		if (this.adminName.equals(userTicket.getUserName())) {
			this.userDataAuthMapper.delete(record);
		} else {
			saveDatas = this.userDataAuthMapper.selectIntersectionUpdateUserDatas(userTicket.getId(), userId, saveDatas);
			this.userDataAuthMapper.deleteUserDataAuth(new HashMap<String, Object>() {
				{
					put("userId", userId);
					put("loggedUserId", userTicket.getId());
				}
			});
		}
		// ???????????????????????????????????????????????????????????????
		if (CollectionUtils.isNotEmpty(saveDatas)) {
			userDataAuthMapper.insertList(saveDatas);
		}
		return BaseOutput.success("????????????").setData(saveDatas);
	}

	private List<UserDataAuth> convertToSaveUserDatas(Long userId, Long dataRange, String[] dataIds) {
		// ??????????????????????????????
		List<UserDataAuth> saveDatas = new ArrayList<>();
		UserDataAuth ud = DTOUtils.newInstance(UserDataAuth.class);
		ud.setUserId(userId);
		ud.setValue(String.valueOf(dataRange));
		ud.setRefCode(DataAuthType.DATA_RANGE.getCode());
		saveDatas.add(ud);
		// ??????????????????????????????????????????
		if (null != dataIds && dataIds.length > 0) {
			for (String id : dataIds) {
				ud = DTOUtils.newInstance(UserDataAuth.class);
				ud.setUserId(userId);
				if (id.startsWith(UapConstants.ALM_PROJECT_PREFIX)) {
					String value = id.replace(UapConstants.ALM_PROJECT_PREFIX, "");
					if (!value.equals("0")) {
						ud.setRefCode(DataAuthType.PROJECT.getCode());
						ud.setValue(value);
						saveDatas.add(ud);
					}
				} else {
					if (id.startsWith(UapConstants.FIRM_PREFIX)) {
						ud.setRefCode(DataAuthType.MARKET.getCode());
						ud.setValue(id.replace(UapConstants.FIRM_PREFIX, ""));
					} else {
						ud.setRefCode(DataAuthType.DEPARTMENT.getCode());
						ud.setValue(id);
					}
					saveDatas.add(ud);
				}
			}
		}
		return saveDatas;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public BaseOutput unlock(Long userId) {
		// ??????-????????????????????????
		User user = DTOUtils.newInstance(User.class);
		user.setId(userId);
		user.setState(UserState.NORMAL.getCode());
		this.updateSelective(user);
		return BaseOutput.success("????????????");
	}

	@Override
	public EasyuiPageOutput listOnlinePage(UserDto user) throws Exception {
		List<String> onlineUserIds = userManager.getOnlineUserIds();
		if (CollectionUtils.isEmpty(onlineUserIds)) {
			return new EasyuiPageOutput(0, Lists.newArrayList());
		}
		user.setIds(onlineUserIds);
		return super.listEasyuiPageByExample(user, true);
	}

	@Override
	public BaseOutput forcedOffline(Long userId) {
		this.userManager.clearUserSession(userId);
		return BaseOutput.success("????????????");
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public int delete(Long id) {
		// ??????????????????????????????
		UserDataAuth userDataAuth = DTOUtils.newInstance(UserDataAuth.class);
		userDataAuth.setUserId(id);
		userDataAuthMapper.delete(userDataAuth);
		// ????????????-????????????
		UserRole userRole = DTOUtils.newInstance(UserRole.class);
		userRole.setUserId(id);
		userRoleMapper.delete(userRole);
		// ??????????????????
		return super.delete(id);
	}

	/**
	 * ???????????????
	 * 
	 * @param passwd
	 * @return
	 */
	private String encryptPwd(String passwd) {
		return md5Util.getMD5ofStr(passwd).substring(6, 24);
	}

	@Override
	public List<UserDepartmentRole> findUserContainDepartmentAndRole(UserDepartmentRoleQuery query) {
		if (query.getDepartmentId() != null && query.getDepartmentId() > 0) {
			Department newDTO = DTOUtils.newInstance(Department.class);
			newDTO.setId(query.getDepartmentId());
			List<Department> depts = this.departmentMapper.select(newDTO);
			if (CollectionUtils.isNotEmpty(depts)) {
				List<Long> ids = new ArrayList<>(depts.size());
				depts.forEach(d -> ids.add(d.getId()));
				query.setDepartmentIds(ids);
			}
		}
		return this.getActualDao().findUserContainDepartmentAndRole(query);
	}

	@Override
	public List<UserDataDto> getUserDataProjectAuthForTree(Long userId) {
		// ?????????????????????????????????????????????
		User user = this.get(userId);
		if (null == user) {
			return null;
		}
		UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
		// ???????????????????????????
		List<UserDataDto> selectAll = new ArrayList<UserDataDto>();
		if (userTicket.getUserName().equalsIgnoreCase(adminName)) {
			selectAll = this.projectRpc.selectUserDataTree().getData();
		} else {
			List<String> selectUserDataAuthValue = userDataAuthMapper.selectUserDataAuthValue(userTicket.getId(), DataAuthType.PROJECT.getCode());
			if (selectUserDataAuthValue != null && selectUserDataAuthValue.size() > 0) {
				BaseOutput<List<UserDataDto>> selectUserDataByIds = this.projectRpc.selectUserDataByIds(selectUserDataAuthValue);
				selectAll = selectUserDataByIds.getData();
			} else {
				return null;
			}
		}
		List<String> selectUserDataAuthValue = userDataAuthMapper.selectUserDataAuthValue(userId, DataAuthType.PROJECT.getCode());
		boolean isRootChecked = false;
		if (selectAll != null && selectAll.size() > 0) {
			if (selectUserDataAuthValue != null && selectUserDataAuthValue.size() > 0) {
				// ????????????
				for (UserDataDto userDataDto : selectAll) {
					String replace = userDataDto.getTreeId().replace(ALM_PROJECT_PREFIX, "");
					boolean isChecked = selectUserDataAuthValue.contains(replace);
					if (!isRootChecked && isChecked) {
						isRootChecked = true;
					}
					userDataDto.setChecked(isChecked);
				}
			}

		}
		UserDataDto almDataDto = DTOUtils.newInstance(UserDataDto.class);
		almDataDto.setTreeId(ALM_PROJECT_PREFIX + 0);
		almDataDto.setName("????????????????????????");
		almDataDto.setChecked(isRootChecked);
		selectAll.add(almDataDto);
		return selectAll;
	}

	@Override
	public List<User> findCurrentFirmUsersByResourceCode(String firmCode, String resourceCode) {
		return this.getActualDao().findCurrentFirmUsersByResourceCode(firmCode, resourceCode);
	}

	@Override
	public BaseOutput<Object> validatePassword(Long userId, String password) {
		User user = this.getActualDao().selectByPrimaryKey(userId);
		if (user == null) {
			return BaseOutput.failure("???????????????");
		}
		// ?????????????????????????????????????????????
		if (user.getState().equals(UserState.LOCKED.getCode())) {
			return BaseOutput.failure("???????????????????????????????????????").setCode(ResultCode.NOT_AUTH_ERROR);
		}
		if (user.getState().equals(UserState.DISABLED.getCode())) {
			return BaseOutput.failure("???????????????????????????????????????!");
		}
		// ??????????????????????????????????????????????????????????????????12?????????????????????
		if (!StringUtils.equals(user.getPassword(), this.encryptPwd(password))) {
			boolean locked = this.loginService.lockUser(user);
			return BaseOutput.failure("????????????????????????").setCode(ResultCode.NOT_AUTH_ERROR).setData(new HashMap<String, Object>() {
				{
					put("locked", locked);
				}
			});
		}
		return BaseOutput.success();
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public BaseOutput saveUserRole(Long userId, Long roleId) {
		if (null == userId) {
			return BaseOutput.failure("??????????????????");
		}
		UserRole userRole = DTOUtils.newInstance(UserRole.class);
		userRole.setUserId(userId);
		userRole.setRoleId(roleId);
		int count = userRoleMapper.selectCount(userRole);
		// ????????????????????????????????????????????????????????????????????????????????????
		if (count == 0) {
			Long id = userRole.getId();
			if (id == null) {
				userRoleMapper.insert(userRole);
			}
		}
		return BaseOutput.success("??????????????????????????????");
	}
}