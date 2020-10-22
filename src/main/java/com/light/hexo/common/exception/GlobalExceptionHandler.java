package com.light.hexo.common.exception;

import com.light.hexo.common.model.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.UnexpectedTypeException;
import java.util.List;
import java.util.Map;

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
            mv = new ModelAndView("jsonView", resultMap);
        } else {
            resultMap.put("contextPath", contextPath);
            mv = new ModelAndView("error", resultMap);
        }

        return mv;
    }

    /**
     * 数据校验异常处理器
     * @param e
     * @return
     */
    @ExceptionHandler
    @ResponseBody
    public Result handleBindException(BindException e) {
        List<ObjectError> allErrors = e.getAllErrors();
        return Result.fail(GlobalExceptionEnum.ERROR_PARAM.getCode(),allErrors.get(0).getDefaultMessage());
    }

}
