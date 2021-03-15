package com.dili.uap.oauth.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

/**
 * UAP oauth动态配置
 */
@RefreshScope
@Component
public class OauthConfig {

	//uap上下文路径
	private String uapContextPath;
	//客户端id
	private String clientId;
	//客户端密钥
	private String clientSecret;
	//oauth登录成功后跳转的controller路径，需要根据request.getAttr中的authUser继续处理
	private String indexPath;
	//oauth登录失败后跳转的controller路径，带有exception异常类为参数
	private String exceptionPath;
	/**
	 * uap上下文路径
	 * @return
	 */
	public String getUapContextPath() {
		return uapContextPath;
	}

	/**
	 * uap上下文路径
	 * @return
	 */
	@Value("${uap.contextPath:http://uap.diligrp.com}")
	public void setUapContextPath(String uapContextPath) {
		this.uapContextPath = uapContextPath;
	}

	/**
	 * 获取客户端id
	 * @return
	 */
	public String getClientId() {
		return clientId;
	}

	/**
	 * 设置客户端id
	 * @param clientId
	 */
	@Value("${oauth.clientId:}")
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	/**
	 * 获取客户端密钥
	 * @return
	 */

	public String getClientSecret() {
		return clientSecret;
	}

	/**
	 * 设置客户端密钥
	 * @param clientSecret
	 */
	@Value("${oauth.clientSecret:}")
	public void setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
	}

	/**
	 * oauth登录成功后跳转的controller路径
	 * @return
	 */
	public String getIndexPath() {
		return indexPath;
	}

	/**
	 * oauth登录成功后跳转的controller路径，需要根据request.getAttr中的authUser继续处理
	 * @param indexPath
	 */
	@Value("${oauth.indexPath:}")
	public void setIndexPath(String indexPath) {
		this.indexPath = indexPath;
	}

	/**
	 * oauth登录失败后跳转的controller路径，带有exception异常类为参数
	 * @return
	 */
	public String getExceptionPath() {
		return exceptionPath;
	}

	/**
	 * oauth登录失败后跳转的controller路径，带有exception异常类为参数
	 * 可以是redirect:开头的重定向
	 * @param exceptionPath
	 */
	@Value("${oauth.exceptionPath:error/default}")
	public void setExceptionPath(String exceptionPath) {
		this.exceptionPath = exceptionPath;
	}
}