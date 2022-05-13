package com.light.hexo.common.exception;

import com.light.hexo.common.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.lang.reflect.UndeclaredThrowableException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * @Author MoonlightL
 * @ClassName: GlobalExceptionHandler
 * @ProjectName hexo-boot
 * @Description: 全局异常处理器
 * @DateTime 2020/7/29 17:03
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @Value("${server.servlet.context-path}")
    private String contextPath;

    /**
     * 自定义异常处理器
     * @param e
     * @return
     */
    @ExceptionHandler
    public ModelAndView handleException(GlobalException e) {

        ModelAndView mv;
        Map<String, Object> resultMap = Result.fail(e.getCode(), e.getMessage()).toMap();

        if (e.getJson()) {
            mv = new ModelAndView("mappingJackson2JsonView", resultMap);
        } else {
            resultMap.put("contextPath", contextPath);
            mv = new ModelAndView("error", resultMap);
        }

        return mv;
    }

    /**
     * 其他异常
     * @param e
     * @return
     */
    @ExceptionHandler
    @ResponseBody
    public Result handleOtherException(Exception e) {
        log.error("======= GlobalExceptionHandler Exception: {} ======", e);

        if (e instanceof BindException) {
            BindException bex = (BindException) e;
            List<ObjectError> allErrors = bex.getAllErrors();
            return Result.fail(GlobalExceptionEnum.ERROR_PARAM.getCode(),allErrors.get(0).getDefaultMessage());

        } else if (e instanceof TimeoutException) {
            return Result.fail(GlobalExceptionEnum.ERROR_TIME_OUT);

        } else if (e instanceof UndeclaredThrowableException) {
            return Result.fail(GlobalExceptionEnum.ERROR_TIME_OUT);

        } else {
            return Result.fail(GlobalExceptionEnum.ERROR_SERVER);

        }
    }
}
