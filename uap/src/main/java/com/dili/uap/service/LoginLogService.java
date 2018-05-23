package com.dili.uap.service;

import com.dili.ss.base.BaseService;
import com.dili.ss.domain.EasyuiPageOutput;
import com.dili.uap.domain.LoginLog;
import com.dili.uap.domain.dto.LoginLogDto;

/**
 * 由MyBatis Generator工具自动生成
 * This file was generated on 2018-05-22 15:30:02.
 */
public interface LoginLogService extends BaseService<LoginLog, Long> {
	public EasyuiPageOutput listEasyuiPageByExample(LoginLogDto domain, boolean useProvider) throws Exception;
}