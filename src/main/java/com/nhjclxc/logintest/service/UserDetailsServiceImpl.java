package com.nhjclxc.logintest.service;

import com.nhjclxc.logintest.handle.AuthenticationContextHolder;
import com.nhjclxc.logintest.model.LoginUser;
import com.nhjclxc.logintest.model.SysUser;
import com.nhjclxc.logintest.utils.ApplicationConstants;
import com.nhjclxc.logintest.utils.CommonUtils;
import com.nhjclxc.logintest.utils.RedisCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 用户验证处理
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private static final Logger log = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    @Autowired
    private RedisCache redisCache;

    @Value(value = "${user.password.maxRetryCount}")
    private int maxRetryCount;

    @Value(value = "${user.password.lockTime}")
    private int lockTime;

    private SysUser selectUserByUserName(String username) {
        String pwd = "root123";
        String password = CommonUtils.encryptPassword(pwd);
        return SysUser.builder().userId(666L).username("admin").password(password).build();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 根据用户名查询用户
        SysUser user = selectUserByUserName(username);
        if (user == null) {
            throw new UsernameNotFoundException("该用户不存在");
        }

        // 验证密码是否正确
        validate(user.getPassword());

        // 创建登录用户
        return new LoginUser(user.getUserId(), user);
    }


    public void validate(String dbPassword) {
        // 下面两个参数是在：new UsernamePasswordAuthenticationToken(username, password) 传入的
        Authentication usernamePasswordAuthenticationToken = AuthenticationContextHolder.getContext();
        String username = usernamePasswordAuthenticationToken.getName();
        String password = usernamePasswordAuthenticationToken.getCredentials().toString();

        Integer retryCount = redisCache.getCacheObject(ApplicationConstants.getPwdErrCountKey(username));

        if (retryCount == null) {
            retryCount = 0;
        }

        if (retryCount >= maxRetryCount) {
            throw new RuntimeException("密码输入错误" + maxRetryCount + "次， 账号被锁定" + lockTime + "分钟");
        }

        boolean flag = CommonUtils.matchesPassword(password, dbPassword);
        if (!flag) {
            retryCount = retryCount + 1;
            redisCache.setCacheObject(ApplicationConstants.getPwdErrCountKey(username), retryCount, lockTime, TimeUnit.MINUTES);
            throw new RuntimeException("用户密码错误");
        } else {

            if (redisCache.hasKey(ApplicationConstants.getPwdErrCountKey(username))) {
                redisCache.deleteObject(ApplicationConstants.getPwdErrCountKey(username));
            }
        }
    }


}
