package com.nhjclxc.logintest.handle;

import com.nhjclxc.logintest.model.LoginUser;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 应用程序上下文句柄
 */
public class ContextHolder {

    /**
     * 这个泛型是你想存放到ThreadLoacl里面的数据的类型
     */
    public static ThreadLocal<LoginUser> authorization = new ThreadLocal<>();


    /**
     * 获取存进来的数据
     */
    public static LoginUser getAuthorization() {
        return authorization.get();
    }

    /**
     * 设置对应的值
     */
    public static void setAuthorization(LoginUser user) {
        authorization.set(user);//往ThreadLocal的对象里面存值
    }

    /**
     * 在退出拦截器之后要使用remove方法来移除数据，即在拦截器的after方法里面调用AuthorizationThreadLocal.remove();
     * 这个方法是为了防止内存溢出
     */

    public static void remove() {
        authorization.remove();
    }


    /**
     * 用户ID
     **/
    public static Long getUserId() {
        try {
            return getLoginUser().getUserId();
        } catch (Exception e) {
            throw new RuntimeException("获取用户ID异常: " + HttpStatus.UNAUTHORIZED.toString());
        }
    }


    /**
     * 获取用户账户
     **/
    public static String getUsername() {
        try {
            return getLoginUser().getUsername();
        } catch (Exception e) {
            throw new RuntimeException("获取用户账户异常" + HttpStatus.UNAUTHORIZED);
        }
    }

    /**
     * 获取用户
     **/
    public static LoginUser getLoginUser() {
        try {
            return ContextHolder.getAuthorization();
        } catch (Exception e) {
            throw new RuntimeException("获取用户信息异常" + HttpStatus.UNAUTHORIZED);
        }
    }

    /**
     * 获取用户
     **/
    public static LoginUser getLoginUser2() {
        try {
            return (LoginUser) getAuthentication().getPrincipal();
        } catch (Exception e) {
            throw new RuntimeException("获取用户信息异常2" + HttpStatus.UNAUTHORIZED);
        }
    }

    /**
     * 获取Authentication
     */
    public static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

}
