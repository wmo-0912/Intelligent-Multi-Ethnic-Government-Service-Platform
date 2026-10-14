package com.example.govservice.exception;

import com.example.govservice.dto.response.CommonResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.converter.HttpMessageNotReadableException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Set;

/**
 * 全局异常处理器
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    /**
     * 处理业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<CommonResponse<Void>> handleBusinessException(BusinessException e) {
        log.error("业务异常：{}", e.getMessage(), e);
        
        // 根据错误码决定HTTP状态码
        HttpStatus httpStatus;
        int code = e.getCode();
        if (code >= 4000 && code < 5000) {
            // 4xxx 错误码返回 400 Bad Request（客户端错误）
            httpStatus = HttpStatus.BAD_REQUEST;
        } else if (code >= 5000) {
            // 5xxx 错误码返回 500 Internal Server Error（服务器错误）
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        } else if (code == 401) {
            httpStatus = HttpStatus.UNAUTHORIZED;
        } else if (code == 403) {
            httpStatus = HttpStatus.FORBIDDEN;
        } else if (code == 404) {
            httpStatus = HttpStatus.NOT_FOUND;
        } else {
            // 默认返回 400
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        
        return ResponseEntity
                .status(httpStatus)
                .body(CommonResponse.error(e.getCode(), e.getMessage()));
    }
    
    /**
     * 处理参数校验异常（@RequestBody）
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CommonResponse<Void> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("参数校验异常：{}", e.getMessage());
        FieldError fieldError = e.getBindingResult().getFieldError();
        String message = fieldError != null ? fieldError.getDefaultMessage() : "参数校验失败";
        return CommonResponse.error(ErrorCode.PARAM_ERROR.getCode(), message);
    }

    /**
     * 处理请求体为空/格式错误
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CommonResponse<Void> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.error("请求体解析异常：{}", e.getMessage());
        return CommonResponse.error(ErrorCode.PARAM_ERROR.getCode(), "请求体为空或格式错误");
    }
    
    /**
     * 处理参数校验异常（@ModelAttribute）
     */
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CommonResponse<Void> handleBindException(BindException e) {
        log.error("参数绑定异常：{}", e.getMessage());
        FieldError fieldError = e.getBindingResult().getFieldError();
        String message = fieldError != null ? fieldError.getDefaultMessage() : "参数绑定失败";
        return CommonResponse.error(ErrorCode.PARAM_ERROR.getCode(), message);
    }
    
    /**
     * 处理参数校验异常（@RequestParam）
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CommonResponse<Void> handleConstraintViolationException(ConstraintViolationException e) {
        log.error("约束校验异常：{}", e.getMessage());
        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
        String message = violations.isEmpty() ? "参数校验失败" : violations.iterator().next().getMessage();
        return CommonResponse.error(ErrorCode.PARAM_ERROR.getCode(), message);
    }
    
    /**
     * 处理数据库异常
     */
    @ExceptionHandler(org.springframework.dao.DataAccessException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public CommonResponse<Void> handleDataAccessException(org.springframework.dao.DataAccessException e) {
        log.error("数据库异常：{}", e.getMessage(), e);
        return CommonResponse.error(ErrorCode.SYSTEM_ERROR.getCode(), "数据库操作失败");
    }
    
    /**
     * 处理其他异常
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public CommonResponse<Void> handleException(Exception e) {
        log.error("系统异常：{}", e.getMessage(), e);
        return CommonResponse.error(ErrorCode.SYSTEM_ERROR.getCode(), "系统内部错误");
    }
}
