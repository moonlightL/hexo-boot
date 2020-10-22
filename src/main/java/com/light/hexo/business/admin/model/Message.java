package com.light.hexo.business.admin.model;

import com.light.hexo.common.component.mybatis.CreateTime;
import com.light.hexo.common.component.mybatis.UpdateTime;
import com.light.hexo.common.util.DateUtil;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @Author MoonlightL
 * @ClassName: Message
 * @ProjectName hexo-boot
 * @Description: 消息
 * @DateTime 2020/9/18 15:00
 */
@Data
@Accessors(chain = true)
@ToString
@Table(name = "t_message")
public class Message implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "JDBC")
    private Integer id;

    /**
     * 内容
     */
    private String content;

    /**
     * 消息类型 1：评论 2：留言
     */
    private Integer msgType;

    /**
     * 是否已读
     */
    @Column(name = "is_read")
    private Boolean read;

    @CreateTime
    private LocalDateTime createTime;

    @UpdateTime
    private LocalDateTime updateTime;

    public String getMessageTime() {
        return DateUtil.ldtToStr(this.createTime);
    }
}
