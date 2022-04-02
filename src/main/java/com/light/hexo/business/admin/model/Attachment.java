package com.light.hexo.business.admin.model;

import com.light.hexo.common.component.mybatis.CreateTime;
import com.light.hexo.common.component.mybatis.UpdateTime;
import com.light.hexo.common.util.DateUtil;
import com.light.hexo.common.util.FileSizeUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * @Author MoonlightL
 * @ClassName: Attachment
 * @ProjectName hexo-boot
 * @Description: 附件
 * @DateTime 2020/9/10 16:05
 */
@Setter
@Getter
@Accessors(chain = true)
@ToString
@Table(name = "t_attachment")
public class Attachment implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @Id
    @GeneratedValue(generator = "JDBC")
    private Integer id;

    /**
     * 文件名称
     */
    private String filename;

    /**
     * 文件原始名称
     */
    private String originalName;

    /**
     * 文件路径
     */
    private String fileUrl;

    /**
     * 缩略图路径
     */
    private String thumbnailUrl;

    /**
     * 本地路径
     */
    private String filePath;

    /**
     * 文件 key (第三方返回)
     */
    private String fileKey;

    /**
     * 内容类型
     */
    private String contentType;

    /**
     * 文件类型 1：普通 2：图片 3：视频
     */
    private Integer fileType;

    /**
     * 文件大小
     */
    private Long fileSize;

    /**
     * 位置 1：本地 2：七牛云 3：OSS 4: COS
     */
    private Integer position;

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

    public String getFileSizeInfo() {
        return FileSizeUtil.getPrintSize(this.fileSize);
    }

    public Date getUploadTime() {
        return DateUtil.ldt2Date(this.createTime);
    }
}
