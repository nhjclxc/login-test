package com.nhjclxc.logintest.controller;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import javax.annotation.Resource;
import javax.imageio.ImageIO;

import com.nhjclxc.logintest.annotation.Anonymous;
import com.nhjclxc.logintest.annotation.RepeatSubmit;
import com.nhjclxc.logintest.handle.ContextHolder;
import com.nhjclxc.logintest.model.LoginUser;
import com.nhjclxc.logintest.service.LoginService;
import com.nhjclxc.logintest.model.LoginBody;
import com.nhjclxc.logintest.utils.ApplicationConstants;
import com.nhjclxc.logintest.utils.CommonUtils;
import com.nhjclxc.logintest.utils.JsonResult;
import com.nhjclxc.logintest.utils.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.FastByteArrayOutputStream;
import org.springframework.web.bind.annotation.*;
import com.google.code.kaptcha.Producer;

/**
 * 控制器
 */
@RestController
@RequestMapping
public class CommonController {
    @Resource(name = "captchaNumberProducer")
    private Producer captchaNumberProducer;
    @Autowired
    private RedisCache redisCache;
    @Autowired
    private LoginService loginService;

    /**
     * 生成验证码
     */
    @GetMapping("/captchaImage")
    public JsonResult<Map<String, Object>> getCaptchaImage() {
        // 保存验证码信息
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        String verifyKey = ApplicationConstants.getCaptchaCodeVerifyKey(uuid);

        // 生成验证码
        String code = captchaNumberProducer.createText();
        BufferedImage image = captchaNumberProducer.createImage(code);

        redisCache.setCacheObject(verifyKey, code, ApplicationConstants.CAPTCHA_EXPIRATION, TimeUnit.MINUTES);
        // 转换流信息写出
        FastByteArrayOutputStream os = new FastByteArrayOutputStream();
        try {
            ImageIO.write(image, "jpg", os);
        } catch (IOException e) {
            return JsonResult.error(e.getMessage());
        }

        // 获取Base64编码器
        String base64String = ApplicationConstants.BASE_64_IMAGE_PREFIX + CommonUtils.encodeToBase64(os.toByteArray());

        return JsonResult.success()
                .put("uuid", uuid)
                .put("img", base64String);
    }

    /**
     * 登录接口
     */
    @PostMapping("/login")
    public JsonResult<Object> login(@RequestBody LoginBody loginBody) {
        //   http://localhost:8080/login
        /*
{
    "username": "admin",
    "password": "root123",
    "code": "1515",
    "uuid": "4a6133bbb11f44f1b16e04802b806507"
}
         */

        return JsonResult.success(loginService.login(loginBody));
    }

    /**
     * 退出登录
     */
//    http://localhost:8080/logout  会执行LogoutSuccessHandlerImpl


    @GetMapping("/test")
    public JsonResult<Object> test() {
        //   http://localhost:8080/test

        String username = ContextHolder.getUsername();
        System.out.println(username);
        LoginUser loginUser = ContextHolder.getLoginUser();
        System.out.println(loginUser);
        return JsonResult.success(loginUser);
    }

    @Anonymous
    @GetMapping("/testAnonymous")
    public JsonResult<Object> testAnonymous() {
        //   http://localhost:8080/testAnonymous

        System.out.println("http://localhost:8080/testAnonymous");
        return JsonResult.success("http://localhost:8080/testAnonymous");
    }

    @RepeatSubmit(interval = 10000, message = "3s内请勿重复提交请求")
    @GetMapping("/testRepeatSubmit")
    public JsonResult<Object> testRepeatSubmit(String str, Integer num) {
        //   http://localhost:8080/testRepeatSubmit

        System.out.println("http://localhost:8080/testRepeatSubmit " + str + " " + num);
        return JsonResult.success(null, "http://localhost:8080/testRepeatSubmit");
    }

}
