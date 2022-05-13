package com.light.hexo.common.event;

import com.light.hexo.common.component.event.BaseEvent;
import com.light.hexo.common.component.event.EventEnum;
import com.light.hexo.common.util.JsonUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import java.util.Map;

/**
 * @Author MoonlightL
 * @ClassName: AttachmentEvent
 * @ProjectName hexo-boot
 * @Description: 附件事件
 * @DateTime 2022/5/9, 0009 19:35
 */
@Setter
@Getter
@Accessors(chain = true)
public class AttachmentEvent extends BaseEvent {

    /**
     * 附件 id
     */
    private Integer id;

    /**
     * 类型
     */
    private Type type;

    private String attachmentJson;

    @Getter
    public enum Type {

        ADD(1, "添加"),
        REMOVE(2, "删除");

        Integer code;
        String remark;

        Type(int code, String remark) {
            this.code = code;
            this.remark = remark;
        }
    }

    public AttachmentEvent(Object source, Type type, Map<String, Object> attachmentMap) {
        super(source);
        this.type = type;
        this.attachmentJson = JsonUtil.obj2String(attachmentMap);
    }

    @Override
    protected String getEventType() {
        return EventEnum.ATTACHMENT.getType();
    }
}
