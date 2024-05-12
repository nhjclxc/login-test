package com.nhjclxc.logintest.handle;

import java.util.Objects;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.nhjclxc.logintest.model.LoginUser;
import com.nhjclxc.logintest.service.TokenService;
import com.nhjclxc.logintest.utils.CommonUtils;
import com.nhjclxc.logintest.utils.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import com.alibaba.fastjson2.JSON;

/**
 * 自定义退出处理类 返回成功
 */
@Configuration
public class LogoutSuccessHandlerImpl implements LogoutSuccessHandler {

    @Autowired
    private TokenService tokenService;

    /**
     * 退出处理
     */
    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        LoginUser loginUser = tokenService.getLoginUser(request);
        if (Objects.nonNull(loginUser)) {
            // 删除用户缓存数据
            tokenService.deleteLoginUser(loginUser.getUuid());
            // 记录用户退出日志
        }
        System.out.println("user.logout.success");
        CommonUtils.renderString(response, JSON.toJSONString(JsonResult.success("user.logout.success")));
    }
}
