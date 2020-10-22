package com.light.hexo.common.component.file;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.InputStream;

/**
 * @Author MoonlightL
 * @ClassName: FileRequest
 * @ProjectName hexo-boot
 * @Description: 文件请求
 * @DateTime 2020/9/10 16:28
 */
@Getter
@Setter
@Accessors(chain = true)
public class FileRequest {

    /**
     * 上传目录
     */
    private String uploadDir;

    /**
     * 文件名称
     */
    private String filename;

    /**
     * 文件原始名称
     */
    private String originalName;

    /**
     * 文件数据字节数组
     */
    private byte[] data;

    /**
     * 文件类型
     */
    private String contentType;

    /**
     * 文件大小
     */
    private Long fileSize;

    /**
     * 文件流
     */
    private InputStream inputStream;

    /**
     * 文件路径
     */
    private String fileUrl;

    /**
     * 七牛云返回的 key
     */
    private String fileKey;

    /**
     * 文件本地路径
     */
    private String filePath;

    /**
     * 文件后缀名
     */
    private String extension;
}
