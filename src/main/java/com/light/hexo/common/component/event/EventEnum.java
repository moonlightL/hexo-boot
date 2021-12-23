package com.light.hexo.common.component.event;

import lombok.Getter;

/**
 * @Author MoonlightL
 * @ClassName: EventEnum
 * @ProjectName hexo-boot
 * @Description: 事件枚举
 * @DateTime 2020/9/16 10:52
 */
@Getter
public enum  EventEnum {

    LOG(1, "日志事件"),
    POST(2, "文章事件"),
    CONFIG(3, "配置事件"),
    VISIT(4, "访问事件"),
    MESSAGE(5, "消息事件"),
    NAV(6, "导航事件"),
    DYNAMIC(7, "动态事件"),
    CATEGORY(8, "分类事件"),
    FRIEND_LINK(9, "友链事件"),
    TAG(10, "标签"),
    ALBUM(11, "专辑"),
    ;

    EventEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    private int code;

    private String message;

}
