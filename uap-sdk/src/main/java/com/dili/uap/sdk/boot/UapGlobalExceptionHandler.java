package com.dili.uap.sdk.boot;

import com.alibaba.fastjson.JSON;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.util.SpringUtil;
import com.dili.uap.sdk.exception.NotAccessPermissionException;
import com.dili.uap.sdk.exception.NotLoginException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 统一异常处理
 */
@ControllerAdvice
public class UapGlobalExceptionHandler {

    /**
     * 未登录异常处理
     * @param e
     * @return
     */
    @ExceptionHandler(NotLoginException.class)
    public String defultExcepitonHandler(HttpServletRequest request, HttpServletResponse response, NotLoginException e) throws IOException {
        e.printStackTrace();
        String requestType = request.getHeader("X-Requested-With");
        if (requestType == null) {
            request.setAttribute("exception", e);
            return SpringUtil.getProperty("error.page.noLogin", "error/uapNoLogin");
        }
        response.setContentType("application/json;charset=UTF-8");
        return JSON.toJSONString(BaseOutput.failure(e.getMessage()));
    }

    /**
     * 无访问权限异常处理
     * @param e
     * @return
     */
    @ExceptionHandler(NotAccessPermissionException.class)
    public String defultExcepitonHandler(HttpServletRequest request, HttpServletResponse response, NotAccessPermissionException e) throws IOException {
        e.printStackTrace();
        String requestType = request.getHeader("X-Requested-With");
        if (requestType == null) {
            request.setAttribute("exception", e);
            return SpringUtil.getProperty("error.page.noAccess", "error/noAccess");
        }
        response.setContentType("application/json;charset=UTF-8");
        return JSON.toJSONString(BaseOutput.failure(e.getMessage()));
    }

    /**
     * 全局异常处理
     * @param e
     * @return
     */
    @ExceptionHandler(Exception.class)
    public String defultExcepitonHandler(HttpServletRequest request, HttpServletResponse response, Exception e) throws IOException {
        e.printStackTrace();
        String requestType = request.getHeader("X-Requested-With");
        if (requestType == null) {
            request.setAttribute("exception", e);
//            response.sendRedirect(basePath + SpringUtil.getProperty("error.page.default", "error/default"));
            return SpringUtil.getProperty("error.page.default", "error/default");
        }
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(JSON.toJSONString(BaseOutput.failure(e.getMessage())));
        return null;
    }

}