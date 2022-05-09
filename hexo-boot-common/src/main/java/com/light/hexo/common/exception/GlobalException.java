package com.light.hexo.common.exception;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @Author MoonlightL
 * @ClassName: GlobalException
 * @ProjectName hexo-boot
 * @Description: 全局异常
 * @DateTime 2020/7/29 17:00
 */
@Setter
@Getter
@ToString
public class GlobalException extends RuntimeException {

    private int code;

    private String message;

    private Boolean json;

    public GlobalException(GlobalExceptionMap exception, Boolean json) {
        super(exception.getMessage());
        this.code = exception.getCode();
        this.message = exception.getMessage();
        this.json = json;
    }

    public GlobalException(int code, String message) {
        super(message);
        this.code = code;
        this.message = message;
        this.json = true;
    }

    public GlobalException(String message) {
        super(message);
        this.code = 500;
        this.message = message;
        this.json = false;
    }

}