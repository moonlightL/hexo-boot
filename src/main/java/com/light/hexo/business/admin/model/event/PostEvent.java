package com.light.hexo.business.admin.model.event;

import com.light.hexo.common.component.event.BaseEvent;
import com.light.hexo.common.component.event.EventEnum;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @Author MoonlightL
 * @ClassName: PostEvent
 * @ProjectName hexo-boot
 * @Description: 文章更新事件
 * @DateTime 2020/9/16 11:28
 */
@Setter
@Getter
@Accessors(chain = true)
public class PostEvent extends BaseEvent {

    /**
     * 文章 id
     */
    private Integer id;

    /**
     * 类型
     */
    private Type type;

    @Getter
    public enum Type {

        READ(1, "浏览"),
        PRAISE(2, "点赞"),
        COMMENT_ADD(3, "新增评论"),
        COMMENT_MINUS(4, "减少评论"),
        POST_NUM(5, "文章数量")
        ;

        Integer code;
        String remark;

        Type(int code, String remark) {
            this.code = code;
            this.remark = remark;
        }
    }

    public PostEvent(Integer id, Type type) {
        this.id = id;
        this.type = type;
    }

    @Override
    protected EventEnum getEventType() {
        return EventEnum.POST;
    }
}
