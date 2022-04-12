package com.light.hexo.common.component.log;

import lombok.Getter;

/**
 * @Author MoonlightL
 * @ClassName: ActionEnum
 * @ProjectName hexo-boot
 * @Description: 日志类型
 * @DateTime 2021/7/7 18:06
 */
@Getter
public enum ActionEnum {

    INSTALL(1, "安装"),
    LOGIN(2, "登录"),
    LOGOUT(3, "注销"),

    ADMIN_ADD(4, "新增"),
    ADMIN_EDIT(5, "编辑"),
    ADMIN_REMOVE(6, "删除"),
    ADMIN_UPLOAD(7 ,"上传"),
    ADMIN_DOWNLOAD(8 ,"下载"),

    PORTAL_COMMENT(50, "评论"),
    ;

    private int code;

    private String message;

    ActionEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public static String getMessageByCode(int code) {
        for (ActionEnum actionEnum : ActionEnum.values()) {
            if (actionEnum.getCode() == code) {
                return actionEnum.getMessage();
            }
        }
        return "";
    }
}
