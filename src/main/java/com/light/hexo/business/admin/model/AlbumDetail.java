package com.light.hexo.business.admin.model;

import com.light.hexo.common.component.mybatis.CreateTime;
import com.light.hexo.common.component.mybatis.UpdateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @Author MoonlightL
 * @ClassName: AlbumDetail
 * @ProjectName hexo-boot
 * @Description: 专辑详情
 * @DateTime 2021/12/21, 0021 15:42
 */
@Setter
@Getter
@Accessors(chain = true)
@ToString
@Table(name = "t_album_detail")
public class AlbumDetail implements Serializable {

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
     * 资源地址
     */
    private String url;

    /**
     * 封面地址
     */
    private String coverUrl;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 备注
     */
    private String remark;

    /**
     * 专辑 id
     */
    private Integer albumId;

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
}
