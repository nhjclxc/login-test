package com.nhjclxc.logintest.service;

import javax.annotation.Resource;

import com.nhjclxc.logintest.handle.AuthenticationContextHolder;
import com.nhjclxc.logintest.model.LoginBody;
import com.nhjclxc.logintest.model.LoginUser;
import com.nhjclxc.logintest.utils.ApplicationConstants;
import com.nhjclxc.logintest.utils.CommonUtils;
import com.nhjclxc.logintest.utils.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 登录校验方法
 */
@Service
public class LoginService {

    @Autowired
    private RedisCache redisCache;

    @Resource
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenService tokenService;

    /**
     * 登录验证
     */
    public Map<String, Object> login(LoginBody loginBody) {
        String username = loginBody.getUsername();
        String password = loginBody.getPassword();

        // 验证码校验
        validateCaptcha(loginBody.getUuid(), loginBody.getCode());

        // 用户名或密码为空 错误
        if (CommonUtils.isEmpty(username) || CommonUtils.isEmpty(password)) {
            throw new RuntimeException("账号或密码不能为空 !!!");
        }

        // 用户验证
        Authentication authentication = null;
        try {
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
            AuthenticationContextHolder.setContext(authenticationToken);
            // 该方法会去调用 UserDetailsServiceImpl.loadUserByUsername
            // 如果 UserDetailsServiceImpl.loadUserByUsername 没有抛出异常说明验证成功，同时返回这个用户的登录信息
            authentication = authenticationManager.authenticate(authenticationToken);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        } finally {
            // 以下是清空： new UsernamePasswordAuthenticationToken(username, password); 保存的数据
            AuthenticationContextHolder.clearContext();
        }
        // security鉴权成功后获取对应的用户数据
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();

        // 记录用户的登录日志
        recordLoginInfo(loginUser.getUserId());

        // 生成token
        String token = tokenService.createToken(loginUser);

        // 返回数据
        Map<String, Object> map = new HashMap<>();
        map.put("token", token);
        map.put("loginUser", loginUser);
        return map;
    }


    /**
     * 校验验证码
     *
     * @param uuid 唯一标识
     * @param code 验证码
     * @return 结果
     */
    public void validateCaptcha(String uuid, String code) {
        if (CommonUtils.isEmpty(uuid)) {
            throw new RuntimeException("缺少验证码uuid");
        }

        if (CommonUtils.isEmpty(code)) {
            throw new RuntimeException("验证码不能为空");
        }

        String verifyKey = ApplicationConstants.getCaptchaCodeVerifyKey(uuid);
        String captcha = redisCache.getCacheObject(verifyKey);
        redisCache.deleteObject(verifyKey);

        if (Objects.isNull(captcha)) {
            throw new RuntimeException("验证码已过期");
        }
        if (!code.equalsIgnoreCase(captcha)) {
            throw new RuntimeException("验证码输入错误");
        }

    }

    /**
     * 记录登录信息
     */
    public void recordLoginInfo(Long userId) {
        System.out.println(userId + "登录日志");
    }
}
