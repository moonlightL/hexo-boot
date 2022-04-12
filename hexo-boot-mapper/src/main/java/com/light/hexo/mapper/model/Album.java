package com.light.hexo.mapper.model;

import com.light.hexo.mapper.annotation.CreateTime;
import com.light.hexo.mapper.annotation.UpdateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @Author MoonlightL
 * @ClassName: Album
 * @ProjectName hexo-boot
 * @Description: 专辑
 * @DateTime 2021/12/21, 0021 15:37
 */
@Setter
@Getter
@Accessors(chain = true)
@ToString
@Table(name = "t_album")
public class Album  implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @Id
    @GeneratedValue(generator = "JDBC")
    private Integer id;

    /**
     * 名称
     */
    private String name;

    /**
     * 访问类型 1: 公开 0: 私密
     */
    private Integer visitType;

    /**
     * 访问密码
     */
    private String visitCode;

    /**
     * 内容类型 1：图片 2：视频
     */
    private Integer detailType;

    /**
     * 封面
     */
    private String coverUrl;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    @CreateTime
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    @UpdateTime
    private LocalDateTime updateTime;

    /**
     * 详情数
     */
    @Transient
    private Integer detailNum;
}
