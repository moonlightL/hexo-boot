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
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;
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
     * 文件 key (第三方返回)，即文件上传时设置的 filename
     */
    @Deprecated
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

    private static final Integer SIZE = 1024;

    public static String getPrintSize(long size) {
        long rest = 0;

        if (size < SIZE) {
            return size + "B";
        } else {
            size /= SIZE;
        }

        if (size < SIZE) {
            return size + "KB";
        } else {
            rest = size % SIZE;
            size /= SIZE;
        }

        if (size < SIZE) {
            size = size * 100;
            return (size / 100) + "." + (rest * 100 / SIZE % 100) + "MB";
        } else {
            size = size * 100 / SIZE;
            return (size / 100) + "." + (size % 100) + "GB";
        }
    }

    public String getFileSizeInfo() {
        return getPrintSize(this.fileSize);
    }

    public Date getUploadTime() {
        return Date.from(this.createTime.atZone(ZoneId.systemDefault()).toInstant());
    }
}
