package com.light.hexo.business.admin.model.event;

import com.light.hexo.common.component.event.BaseEvent;
import com.light.hexo.common.component.event.EventEnum;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @Author MoonlightL
 * @ClassName: MessageEvent
 * @ProjectName hexo-boot
 * @Description: 消息（评论/留言）事件
 * @DateTime 2020/9/21 17:08
 */
@Setter
@Getter
@Accessors(chain = true)
public class MessageEvent extends BaseEvent {

    /**
     * 消息内容
     */
    private String content;

    /**
     * 类型
     */
    private Type type;

    @Getter
    public enum Type {

        POST_COMMENT(1, "评论"),
        GUEST_BOOK(2, "留言");

        Integer code;
        String remark;

        Type(int code, String remark) {
            this.code = code;
            this.remark = remark;
        }
    }

    public MessageEvent(Object source, String content, Type type) {
        this.content = content;
        this.type = type;
    }

    @Override
    protected EventEnum getEventType() {
        return EventEnum.MESSAGE;
    }
}
