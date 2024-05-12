package com.nhjclxc.logintest.handle;

import java.io.IOException;
import java.io.Serializable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.nhjclxc.logintest.utils.CommonUtils;
import com.nhjclxc.logintest.utils.JsonResult;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import com.alibaba.fastjson2.JSON;

/**
 * 认证失败处理类 返回未授权
 */
@Component
public class AuthenticationEntryPointImpl implements AuthenticationEntryPoint, Serializable {
    private static final long serialVersionUID = -8970718410437077606L;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) {
        HttpStatus code = HttpStatus.UNAUTHORIZED;
        String msg = String.format("请求访问：%s，认证失败，无法访问系统资源", request.getRequestURI());
        CommonUtils.renderString(response, JSON.toJSONString(JsonResult.error(code.value(), msg)));
    }

}
