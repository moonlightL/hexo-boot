package com.light.hexo.common.response;

import lombok.Getter;
import lombok.Setter;

/**
 * @Author MoonlightL
 * @ClassName: ApiResponse
 * @Description: 接口响应
 * @DateTime 2024/12/3 10:28
 */
@Setter
@Getter
public class ApiResponse<T>{

    private int code;

    private T data;

    public boolean isSuccess() {
        return this.code == 200;
    }
}
