package com.nhjclxc.logintest.handle;

import org.springframework.security.core.Authentication;

/**
 * 身份验证信息 登录时使用，不用和ContextHolder混合使用，解耦
 */
public class AuthenticationContextHolder {
    private static final ThreadLocal<Authentication> contextHolder = new ThreadLocal<>();

    public static Authentication getContext() {
        return contextHolder.get();
    }

    public static void setContext(Authentication context) {
        contextHolder.set(context);
    }

    public static void clearContext() {
        contextHolder.remove();
    }
}
