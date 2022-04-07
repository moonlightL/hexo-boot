package com.light.hexo.common.component.file;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.commons.io.FilenameUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
     * 文件流
     */
    private InputStream inputStream;

    /**
     * 封面（base64类型）
     */
    private String coverBase64;

    /**
     * 文件类型
     */
    private String contentType;

    /**
     * 文件大小
     */
    private Long fileSize;

    /**
     * 文件路径
     */
    private String fileUrl;

    /**
     * 七牛云返回的 key
     */
    @Deprecated
    private String fileKey;

    /**
     * 文件本地路径
     */
    private String filePath;

    /**
     * 文件后缀名
     */
    private String extension;


    public static FileRequest createRequest(MultipartFile file) {
        FileRequest fileRequest = null;
        try {
            fileRequest = new FileRequest();
            String originalName = file.getOriginalFilename();
            String baseName = FilenameUtils.getBaseName(originalName);
            String extension = FilenameUtils.getExtension(originalName);
            String newFilename = baseName + "_" + System.currentTimeMillis() + "." + extension;
            fileRequest.setOriginalName(originalName)
                       .setFilename(newFilename)
                       .setData(file.getBytes())
                       .setInputStream(file.getInputStream())
                       .setFileSize(file.getSize())
                       .setContentType(file.getContentType())
                       .setExtension(extension);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileRequest;
    }
}
