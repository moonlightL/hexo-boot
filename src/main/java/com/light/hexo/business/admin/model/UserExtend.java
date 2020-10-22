package com.light.hexo.business.admin.model;

import com.light.hexo.common.component.mybatis.CreateTime;
import com.light.hexo.common.component.mybatis.UpdateTime;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @Author MoonlightL
 * @ClassName: UserExtend
 * @ProjectName hexo-boot
 * @Description: 用户信息扩展
 * @DateTime 2020/9/28 13:59
 */
@Data
@Accessors(chain = true)
@ToString
@Table(name = "t_user_extend")
public class UserExtend implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "JDBC")
    private Integer id;

    /**
     * 用户 id
     */
    private Integer uid;

    /**
     * 自我介绍
     */
    private String descr;

    @CreateTime
    private LocalDateTime createTime;

    @UpdateTime
    private LocalDateTime updateTime;
}
