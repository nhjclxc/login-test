package com.nhjclxc.logintest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

@SpringBootApplication
public class LoginTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(LoginTestApplication.class, args);
    }

}
