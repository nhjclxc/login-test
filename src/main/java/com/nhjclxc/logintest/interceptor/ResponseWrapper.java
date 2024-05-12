package com.nhjclxc.logintest.interceptor;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * 构建可重复读取响应
 */
public class ResponseWrapper extends HttpServletResponseWrapper {
    private final ByteArrayOutputStream byteArrayOutputStream;

    public ResponseWrapper(HttpServletResponse response) {
        super(response);
        byteArrayOutputStream=new ByteArrayOutputStream();
    }

    @Override
    public ServletOutputStream getOutputStream() {
        return new ServletOutputStream() {
            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setWriteListener(WriteListener writeListener) {

            }

            @Override
            public void write(int b) {
                byteArrayOutputStream.write(b);
            }
        };
    }

    public byte[] toByteArray(){
        return byteArrayOutputStream.toByteArray();
    }
}
