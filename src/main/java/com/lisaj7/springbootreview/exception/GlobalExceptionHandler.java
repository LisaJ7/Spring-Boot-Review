/*
全局异常处理器
* */
package com.lisaj7.springbootreview.exception;

import com.lisaj7.springbootreview.common.Result;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// 这个注解表示：这是一个专门处理所有 Controller 异常的全局组件
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 这个注解表示：如果出现 Exception，就交给这个方法处理
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        return Result.fail("服务器内部错误");
    }

    // 参数校验失败通常抛出MethodArgumentNotValidException
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleValidationException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldError().getDefaultMessage();

        return Result.fail(message);
    }
}
