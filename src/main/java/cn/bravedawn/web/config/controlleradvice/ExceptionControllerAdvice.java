package cn.bravedawn.web.config.controlleradvice;

import cn.bravedawn.web.common.CommonResult;
import cn.bravedawn.web.exception.BusinessException;
import cn.bravedawn.web.exception.SystemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author : depers
 * @program : jasper
 * @description:
 * @date : Created in 2023/4/8 16:58
 */

@RestControllerAdvice
public class ExceptionControllerAdvice {

    private static final Logger log = LoggerFactory.getLogger(ExceptionControllerAdvice.class);

    @ExceptionHandler(BusinessException.class)
    public CommonResult<?> businessExceptionHandler(BusinessException businessException) {
        log.error("发生业务异常, error msg={}.", businessException.getMessage());
        return CommonResult.FAILURE(businessException);
    }


    @ExceptionHandler(SystemException.class)
    public CommonResult<?> systemExceptionHandler(SystemException systemException) {
        log.error("发生系统异常, error msg={}.", systemException.getMessage());
        return CommonResult.FAILURE();
    }


    @ExceptionHandler(Exception.class)
    public CommonResult<?> systemExceptionHandler(Exception exception) {
        log.error("发生其他异常.", exception);
        return CommonResult.FAILURE();
    }
}
