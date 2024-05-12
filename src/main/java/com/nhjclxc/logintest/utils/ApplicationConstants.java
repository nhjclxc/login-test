package com.nhjclxc.logintest.utils;

/**
 * Title: 常量类 <br>
 */
public class ApplicationConstants {

    /**
     * 状态（0=表示否定,1=表示确定）
     */
    public enum BaseStatus {
        DEACTIVATE(0, "0", "表示否定"),
        ACTIVATE(1, "1", "表示确定");
        public final Integer key;
        public final String keyStr;
        public final String value;

        BaseStatus(Integer key, String keyStr, String value) {
            this.value = value;
            this.keyStr = keyStr;
            this.key = key;
        }
    }

    public static final String BASE_64_IMAGE_PREFIX = "data:image/png;base64,";
    /**
     * 验证码有效期（分钟）
     */
    public static final Integer CAPTCHA_EXPIRATION = 3;

    /**
     * 验证码 redis key
     */
    private static final String CAPTCHA_CODE_KEY = "captcha_codes:";

    /**
     * 令牌前缀
     */
    public static final String TOKEN_PREFIX = "Bearer ";

    /**
     * token里面保存用户数据的标识
     */
    public static final String LOGIN_USER_UUID_KEY = "login_user_uuid_key";

    /**
     * 登录用户 redis key
     */
    private static final String CACHE_LOGIN_UUID = "login_uuid:";

    /**
     * 登录账户密码错误次数 redis key
     */
    private static final String PWD_ERR_COUNT_KEY = "pwd_err_count:";
    /**
     * 防重提交 redis key
     */
    public static final String REPEAT_SUBMIT_KEY = "repeat_submit:";

    /**
     * 获取tokenKey
     */
    public static String getRepeatSubmitKey(String url, String submitKey) {
        return ApplicationConstants.REPEAT_SUBMIT_KEY + url + "_" + submitKey;
    }
    /**
     * 获取tokenKey
     */
    public static String getCacheLoginUUidKey(String uuid) {
        return ApplicationConstants.CACHE_LOGIN_UUID + uuid;
    }

    /**
     * 获取验证码Key
     */
    public static String getCaptchaCodeVerifyKey(String uuid) {
        return ApplicationConstants.CAPTCHA_CODE_KEY + uuid;
    }

    /**
     * 登录账户密码错误次数缓存键名
     *
     * @param username 用户名
     * @return 缓存键key
     */
    public static String getPwdErrCountKey(String username) {
        return ApplicationConstants.PWD_ERR_COUNT_KEY + username;
    }


}
