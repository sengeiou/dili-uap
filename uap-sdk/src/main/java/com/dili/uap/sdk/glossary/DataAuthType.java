package com.dili.uap.sdk.glossary;

/**
 * 统一系统的数据权限类型 Created by asiam on 2018/5/24.
 */
public enum DataAuthType {
	// 数据范围
	DATA_RANGE("dataRange", "数据范围"),
	// 用户和部门关系的数据权限
	DEPARTMENT("department", "部门"),
	// 用户和市场关系的数据权限
	MARKET("market", "市场"),
	//客户类型
	CUSTOMER_TYPE("customerType", "客户类型"),
	// 项目关系的数据权限
	PROJECT("project", "项目"), TRADING_HALL("trading_hall", "交易厅");

	private String name;
	// 对应数据权限表中的type
	private String code;

	DataAuthType(String code, String name) {
		this.code = code;
		this.name = name;
	}

	public static DataAuthType getDataAuthType(String code) {
		for (DataAuthType dataAuthType : DataAuthType.values()) {
			if (dataAuthType.getCode().equals(code)) {
				return dataAuthType;
			}
		}
		return null;
	}

	public String getCode() {
		return code;
	}

	public String getName() {
		return name;
	}
}
