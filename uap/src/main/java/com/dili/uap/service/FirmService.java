package com.dili.uap.service;

import com.dili.ss.base.BaseService;
import com.dili.ss.domain.BaseOutput;
import com.dili.uap.sdk.domain.Firm;

/**
 * 由MyBatis Generator工具自动生成
 * This file was generated on 2018-05-23 14:31:07.
 */
public interface FirmService extends BaseService<Firm, Long> {

	BaseOutput<Object> insertAndBindUserDataAuth(Firm firm);
}