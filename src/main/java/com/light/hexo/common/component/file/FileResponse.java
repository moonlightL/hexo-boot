package com.light.hexo.common.component.file;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @Author MoonlightL
 * @ClassName: FileResponse
 * @ProjectName hexo-boot
 * @Description: 文件响应
 * @DateTime 2020/9/10 16:28
 */
@Getter
@Setter
@Accessors(chain = true)
public class FileResponse {

    /**
     * 是否成功
     */
    private boolean success;

    /**
     * 七牛云返回的 key
     */
    @Deprecated
    private String key;

    /**
     * 七牛云返回的 hash
     */
    private String hash;

    /**
     * 文件访问地址
     */
    private String url;

    /**
     * 文件本地路径
     */
    private String path;

    /**
     * 文件数据字节数组
     */
    private byte[] data;

    /**
     * 文件名称
     */
    private String filename;

    /**
     * 文件原始名称
     */
    private String originalName;

    /**
     * 封面
     */
    private String coverUrl;

    /**
     * 错误信息
     */
    private String errorMsg;

}
