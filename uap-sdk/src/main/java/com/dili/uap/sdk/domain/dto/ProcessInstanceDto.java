package com.dili.uap.sdk.domain.dto;

public interface ProcessInstanceDto {

	/**
	 * 流程实例id
	 * 
	 * @return
	 */
	String getProcessInstanceId();

	void setProcessInstanceId(String processInstanceId);

	/**
	 * 流程定义id
	 * 
	 * @return
	 */
	String getProcessDefinitionId();

	void setProcessDefinitionId(String processDefinitionId);
}