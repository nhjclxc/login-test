package com.nhjclxc.logintest.config;

import com.google.code.kaptcha.text.impl.DefaultTextCreator;

import java.security.SecureRandom;
import java.util.Random;

/**
 * 验证码文本生成器
 */
public class KaptchaNumberCreator extends DefaultTextCreator {
    private static final char[] chars = {'0', '1', '2', '5', '6', '8', '9'};

    @Override
    public String getText() {
        int length = this.getConfig().getTextProducerCharLength();
        Random rand = new SecureRandom();
        StringBuilder numberStr = new StringBuilder();
        for (int i = 0; i < length; ++i) {
            numberStr.append(chars[rand.nextInt(chars.length)]);
        }
        return numberStr.toString();
    }
}
