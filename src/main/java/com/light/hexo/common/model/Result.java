package com.light.hexo.common.model;

import com.light.hexo.common.exception.GlobalExceptionMap;
import lombok.Getter;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author MoonlightL
 * @ClassName: Result
 * @ProjectName hexo-boot
 * @Description: 返回结果，默认 code 200：成功  500：失败
 * @DateTime 2020/7/29 16:24
 */
@Getter
public class Result implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer code;

    private String message;

    private Object data;

    private Boolean success;

    private Result(Integer code, String message) {
        this.code = code;
        this.message = message;
        this.success = this.code.equals(200);
    }

    private Result(Integer code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.success = this.code.equals(200);
    }

    private Result(GlobalExceptionMap exceptionMap) {
        this.code = exceptionMap.getCode();
        this.message = exceptionMap.getMessage();
        this.success = this.code.equals(200);
    }

    public static Result success() {
        return new Result(200, "success");
    }

    public static <T> Result success(T data) {
        return new Result(200, "success", data);
    }

    public static Result fail() {
        return new Result(500, "fail");
    }

    public static Result fail(int code, String message) {
        return new Result(code, message);
    }

    public static Result fail(GlobalExceptionMap exceptionMap) {
        return new Result(exceptionMap);
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>(4);
        map.put("code", code);
        map.put("success", success);
        map.put("message", message);
        map.put("data", data);
        return map;
    }
}
