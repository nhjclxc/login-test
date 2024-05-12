package com.nhjclxc.logintest.utils;


import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Base64;

public class CommonUtils {

    private CommonUtils() {
    }

    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    public static boolean isEmpty(String str) {
        return str == null || "".equals(str);
    }

    /**
     * 将流数据编码为 Base64 字符串
     */
    public static String encodeToBase64(byte[] imageData) {
        return Base64.getEncoder().encodeToString(imageData);
    }

    /**
     * 将Base64 字符串编码为 流数据
     */
    public static byte[] encodeToStream(String base64Text) {
        return Base64.getDecoder().decode(base64Text);
    }


    /**
     * 生成BCryptPasswordEncoder密码
     *
     * @param password 密码
     * @return 加密字符串
     */
    public static String encryptPassword(String password) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.encode(password);
    }

    /**
     * 判断密码是否相同
     *
     * @param rawPassword 原始密码（用户输入的密码）
     * @param encodedPassword 加密后的密码（数据库里面存着的密码）
     * @return 结果
     */
    public static boolean matchesPassword(String rawPassword, String encodedPassword) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }



    /**
     * 将字符串渲染到客户端
     *
     * @param response 渲染对象
     * @param string   待渲染的字符串
     */
    public static void renderString(HttpServletResponse response, String string) {
        try {
            response.setStatus(200);
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            response.getWriter().print(string);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
