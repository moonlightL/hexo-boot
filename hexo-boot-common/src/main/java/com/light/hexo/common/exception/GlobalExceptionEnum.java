package com.light.hexo.common.exception;

import lombok.Getter;

/**
 * @Author MoonlightL
 * @ClassName: GlobalExceptionEnum
 * @ProjectName hexo-boot
 * @Description: 全局异常枚举
 * @DateTime 2020/7/29 17:07
 */
@Getter
public enum GlobalExceptionEnum implements GlobalExceptionMap {

    ERROR_USER_NOT_EXIST(100, "用户不存在"),
    ERROR_PARAM(400, "请求参数有误"),
    ERROR_UNAUTHORIZED(401, "用户未授权"),
    ERROR_FORBIDDEN(403, "资源被禁止访问"),
    ERROR_SERVER(500, "系统异常"),
    ERROR_TIME_OUT(502, "超时操作"),
    ERROR_VERIFY_CODE_WRONG(600, "验证码不正确"),
    ERROR_NOT_LOGIN_TO_COMMENT(601, "请先登录再进行评论"),
    ERROR_USER_STATE_NOT_VALID(602, "账户异常，请联系博主"),
    ERROR_CAN_NOT_DELETE_RESOURCE(900, "默认资源无法删除"),
    ERROR_IN_BLACKLIST(901, "检测到你进行非法操作，已被列入黑名单!");

    private int code;

    private String message;

    GlobalExceptionEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }
}