package com.nhjclxc.logintest.service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import javax.servlet.http.HttpServletRequest;

import com.nhjclxc.logintest.model.LoginUser;
import com.nhjclxc.logintest.utils.ApplicationConstants;
import com.nhjclxc.logintest.utils.RedisCache;
import io.jsonwebtoken.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * token验证处理
 */
@Component
public class TokenService {
    private static final Logger log = LoggerFactory.getLogger(TokenService.class);


    private static final long MILLIS_SECOND = 1000;

    private static final long MILLIS_MINUTE = 60 * MILLIS_SECOND;

    private static final Long MILLIS_MINUTE_TEN = 20 * 60 * 1000L;
    /**
     * 令牌自定义标识
     */
    @Value("${token.header}")
    private String Authorization;

    /**
     * 令牌秘钥
     */
    @Value("${token.secret}")
    private String secret;

    /**
     * 令牌有效期
     */
    @Value("${token.expireTime}")
    private int expireTime;

    @Autowired
    private RedisCache redisCache;

    /**
     * 获取用户身份信息
     *
     * @return 用户信息
     */
    public LoginUser getLoginUser(HttpServletRequest request) {
        return getLoginUser(request.getHeader(Authorization));
    }

    public LoginUser getLoginUser(String requestToken) {
        // 获取请求携带的令牌
        String token = getToken(requestToken);
        if (StringUtils.isNotEmpty(token)) {
            try {
                Claims claims = parseToken(token);
                // 解析对应的权限以及用户信息
                String uuid = (String) claims.get(ApplicationConstants.LOGIN_USER_UUID_KEY);
                String userKey = ApplicationConstants.getCacheLoginUUidKey(uuid);
                return redisCache.getCacheObject(userKey);
            } catch (Exception e) {
                log.error("获取用户信息异常'{}'", e.getMessage());
            }
        }
        return null;
    }

    /**
     * 设置用户身份信息
     */
    public void setLoginUser(LoginUser loginUser) {
        // 本方法是用户数据被更改之后调用的，用来刷新redis里面的用户数据
        if (Objects.nonNull(loginUser) && StringUtils.isNotEmpty(loginUser.getUuid())) {
            refreshToken(loginUser);
        }
    }

    /**
     * 删除用户身份信息
     */
    public void deleteLoginUser(String uuid) {
        if (StringUtils.isNotEmpty(uuid)) {
            String userKey = ApplicationConstants.getCacheLoginUUidKey(uuid);
            redisCache.deleteObject(userKey);
        }
    }

    /**
     * 创建令牌
     *
     * @param loginUser 用户信息
     * @return 令牌
     */
    public String createToken(LoginUser loginUser) {
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        loginUser.setUuid(uuid);

        Map<String, Object> claims = new HashMap<>();
        claims.put(ApplicationConstants.LOGIN_USER_UUID_KEY, uuid);
        String token = doCreateToken(claims);

        // 在redis里面刷新token
        refreshToken(loginUser);

        return token;
    }

    /**
     * 验证令牌有效期，相差不足20分钟，自动刷新缓存
     */
    public void verifyToken(LoginUser loginUser) {
        long expireTime = loginUser.getExpireTime();
        long currentTime = System.currentTimeMillis();
        if (expireTime - currentTime <= MILLIS_MINUTE_TEN) {
            refreshToken(loginUser);
        }
    }

    /**
     * 刷新令牌有效期
     *
     * @param loginUser 登录信息
     */
    public void refreshToken(LoginUser loginUser) {
        loginUser.setLoginTime(System.currentTimeMillis());
        loginUser.setExpireTime(loginUser.getLoginTime() + expireTime * MILLIS_MINUTE);
        // 根据uuid将loginUser缓存
        String userUuidKey = ApplicationConstants.getCacheLoginUUidKey(loginUser.getUuid());
        redisCache.setCacheObject(userUuidKey, loginUser, expireTime, TimeUnit.MINUTES);
    }


    /**
     * 获取请求token
     *
     * @return token
     */
    private String getToken(String requestToken) {
        String token = requestToken;
        if (StringUtils.isNotEmpty(token) && token.startsWith(ApplicationConstants.TOKEN_PREFIX)) {
            token = token.replace(ApplicationConstants.TOKEN_PREFIX, "");
        }
        return token;
    }


    /**
     * 从数据声明生成令牌
     *
     * @param claims 数据声明
     * @return 令牌
     */
    private String doCreateToken(Map<String, Object> claims) {
        //返回token
        return Jwts.builder()
                //设置头
                .setHeaderParam("typ", "JWT")
                .setHeaderParam("alg", "HS256")
                //设置payload
                .setClaims(claims)  //设置用户信息
                .setSubject("userLoginToken")
                .setExpiration(new Date(System.currentTimeMillis() + expireTime * MILLIS_MINUTE))
                .setId(UUID.randomUUID().toString())
                //设置签名
                .signWith(SignatureAlgorithm.HS256, secret)
                //连接
                .compact();
    }

    /**
     * 从令牌中获取数据声明
     *
     * @param token 令牌
     * @return 数据声明
     */
    private Claims parseToken(String token) {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
    }
}
