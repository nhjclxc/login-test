package com.nhjclxc.logintest.exception;

import javax.servlet.http.HttpServletRequest;

import com.nhjclxc.logintest.utils.JsonResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 全局异常处理器
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 权限校验异常
     */
    @ExceptionHandler(AccessDeniedException.class)
    public JsonResult<Object> handleAccessDeniedException(AccessDeniedException e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.error("请求地址'{}',权限校验失败'{}'", requestURI, e.getMessage());
        return JsonResult.error(HttpStatus.FORBIDDEN.value(), "没有权限，请联系管理员授权");
    }

    /**
     * 请求方式不支持
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public JsonResult<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException e,
                                                                  HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.error("请求地址'{}',不支持'{}'请求", requestURI, e.getMethod());
        return JsonResult.error(e.getMessage());
    }

//    /**
//     * 业务异常
//     */
//    @ExceptionHandler(ServiceException.class)
//    public JsonResult<Object> handleServiceException(ServiceException e, HttpServletRequest request) {
//        log.error(e.getMessage(), e);
//        Integer code = e.getCode();
//        return StringUtils.isNotNull(code) ? JsonResult.error(code, e.getMessage()) : JsonResult.error(e.getMessage());
//    }

    /**
     * 请求路径中缺少必需的路径变量
     */
    @ExceptionHandler(MissingPathVariableException.class)
    public JsonResult<Object> handleMissingPathVariableException(MissingPathVariableException e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.error("请求路径中缺少必需的路径变量'{}',发生系统异常.", requestURI, e);
        return JsonResult.error(String.format("请求路径中缺少必需的路径变量[%s]", e.getVariableName()));
    }

    /**
     * 请求参数类型不匹配
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public JsonResult<Object> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.error("请求参数类型不匹配'{}',发生系统异常.", requestURI, e);
        return JsonResult.error(String.format("请求参数类型不匹配，参数[%s]要求类型为：'%s'，但输入值为：'%s'", e.getName(), e.getRequiredType().getName(), e.getValue()));
    }

    /**
     * 拦截未知的运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    public JsonResult<Object> handleRuntimeException(RuntimeException e, HttpServletRequest request) {
        // 把不是com.nhjclxc的堆栈过滤掉，排除一些不必要的信息，
        List<String> stackTraceList = new ArrayList<>();
        for (StackTraceElement stackTraceElement : e.getStackTrace()) {
            String s = stackTraceElement.toString();
            if (s.contains("com.nhjclxc")) {
                stackTraceList.add(s);
            }
        }
        String requestURI = request.getRequestURI();
        log.error("请求地址'{}',发生未知异常.", requestURI, e);
        return JsonResult.error("请求地址'" + requestURI + "',发生异常" + e.toString() + "。异常堆栈：" + stackTraceList.toString());
    }

    /**
     * 系统异常
     */
    @ExceptionHandler(Exception.class)
    public JsonResult<Object> handleException(Exception e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.error("请求地址'{}',发生系统异常.", requestURI, e);
        return JsonResult.error(e.getMessage());
    }


}
