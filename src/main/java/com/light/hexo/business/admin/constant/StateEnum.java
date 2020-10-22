package com.light.hexo.business.admin.constant;

import lombok.Getter;

/**
 * @Author MoonlightL
 * @ClassName: StateEnum
 * @ProjectName hexo-boot
 * @Description: 状态枚举
 * @DateTime 2020/9/9 11:24
 */
@Getter
public enum StateEnum {

    ON(1, "开启"),
    OFF(0, "关闭");

    private Integer code;

    private String message;

    StateEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

}
