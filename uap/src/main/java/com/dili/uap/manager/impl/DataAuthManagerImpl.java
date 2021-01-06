package com.dili.uap.manager.impl;

import com.alibaba.fastjson.JSON;
import com.dili.ss.dto.DTOUtils;
import com.dili.uap.dao.UserDataAuthMapper;
import com.dili.uap.manager.DataAuthManager;
import com.dili.uap.sdk.domain.UserDataAuth;
import com.dili.uap.sdk.glossary.SystemType;
import com.dili.uap.sdk.session.DynaSessionConstants;
import com.dili.uap.sdk.util.KeyBuilder;
import com.dili.uap.sdk.util.ManageRedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 数据权限redis管理器 Created by asiam
 */
@Component
public class DataAuthManagerImpl implements DataAuthManager {
	private final static Logger LOG = LoggerFactory.getLogger(DataAuthManagerImpl.class);

	@Autowired
	private UserDataAuthMapper userDataAuthMapper;

	@Autowired
	private ManageRedisUtil redisUtil;

	@Autowired
	private DynaSessionConstants dynaSessionConstants;

	@Override
	public void initWebUserDataAuthesInRedis(Long userId) {
		initUserDataAuthesInRedis(userId, SystemType.WEB.getCode());
	}

	@Override
	public void initAppUserDataAuthesInRedis(Long userId) {
		initUserDataAuthesInRedis(userId, SystemType.APP.getCode());
	}

	@Override
	public List<UserDataAuth> listUserDataAuthesByRefCode(Long userId, String refCode) {
		UserDataAuth userDataAuth = DTOUtils.newInstance(UserDataAuth.class);
		userDataAuth.setUserId(userId);
		userDataAuth.setRefCode(refCode);
		return this.userDataAuthMapper.select(userDataAuth);
	}

	@Override
	public List<UserDataAuth> listUserDataAuthes(Long userId) {
		UserDataAuth userDataAuth = DTOUtils.newInstance(UserDataAuth.class);
		userDataAuth.setUserId(userId);
		return this.userDataAuthMapper.select(userDataAuth);
	}

	/**
	 * 初始化数据权限到redis
	 * @param userId
	 * @param systemType
	 */
	public void initUserDataAuthesInRedis(Long userId, Integer systemType) {
		UserDataAuth userDataAuth = DTOUtils.newInstance(UserDataAuth.class);
		userDataAuth.setUserId(userId);
		// 查询数据权限，需要合并下面的部门数据权限列表
		List<UserDataAuth> userDataAuths = this.userDataAuthMapper.select(userDataAuth);
		String key = KeyBuilder.buildUserDataAuthKey(userId.toString(), systemType);
		this.redisUtil.remove(key);
		if (CollectionUtils.isEmpty(userDataAuths)) {
			return;
		}
		BoundSetOperations<String, Object> ops = this.redisUtil.getRedisTemplate().boundSetOps(key);
		Long sessionTimeout = SystemType.WEB.getCode().equals(systemType) ? dynaSessionConstants.getWebRefreshTokenTimeout() : dynaSessionConstants.getAppRefreshTokenTimeout();
		ops.expire(sessionTimeout, TimeUnit.SECONDS);
		for (UserDataAuth dataAuth : userDataAuths) {
			ops.add(JSON.toJSONString(dataAuth));
		}
	}

}