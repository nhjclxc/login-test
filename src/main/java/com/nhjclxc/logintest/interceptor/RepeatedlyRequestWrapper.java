package com.nhjclxc.logintest.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StreamUtils;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.Map;
import java.util.Vector;

/**
 * 构建可重复读取inputStream的request
 */
@Slf4j
public class RepeatedlyRequestWrapper extends HttpServletRequestWrapper {

    private String body;

    private byte[] bytes;

    private final Map<String, String[]> params;

    private boolean hasBody;

    public RepeatedlyRequestWrapper(HttpServletRequest request, Map<String, String[]> params) throws IOException {
        super(request);
        this.params = params;
        try (ServletInputStream inputStream = request.getInputStream()) {
            int contentLength = request.getContentLength();
            if (contentLength == -1) {
                bytes = new byte[]{};
                return;
            }
            bytes = StreamUtils.copyToByteArray(inputStream);
            body = new String(this.bytes);
            hasBody = false;
            for (byte i : this.bytes) {
                if (i != 0) {
                    hasBody = true;
                    break;
                }
            }
        }
    }

    @Override
    public ServletInputStream getInputStream() {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        return new ServletInputStream() {
            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setReadListener(ReadListener readListener) {

            }

            @Override
            public int read() {
                return byteArrayInputStream.read();
            }
        };
    }

    /**
     * 针对MultiRequestBody注解的处理
     */
    @Override
    public BufferedReader getReader() {
        return new BufferedReader(new InputStreamReader(this.getInputStream()));
    }

    @Override
    public String getParameter(String name) {
        String[] values = params.get(name);
        if (values == null || values.length <= 0) {
            return null;
        } else {
            return values[0];
        }
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        return params;
    }

    @Override
    public Enumeration<String> getParameterNames() {
        Vector<String> names = new Vector<>(params.keySet());
        return names.elements();
    }

    @Override
    public String[] getParameterValues(String name) {
        return params.get(name);
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    public boolean isHasBody() {
        return hasBody;
    }
}
