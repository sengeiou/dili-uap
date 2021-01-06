package com.dili.uap.domain.dto;

import com.dili.ss.dto.IDTO;
import com.dili.uap.sdk.domain.User;

/**
 * 登录结果
 * Created by asiam on 2018/5/18 0018.
 */
public interface LoginResult extends IDTO {

    //访问token
    String getAccessToken();
    void setAccessToken(String accessToken);

    //刷新token
    String getRefreshToken();
    void setRefreshToken(String refreshToken);

    //登录成功后的返回地址
    String getLoginPath();
    void setLoginPath(String loginPath);

    //登录用户
    User getUser();
    void setUser(User user);
}
