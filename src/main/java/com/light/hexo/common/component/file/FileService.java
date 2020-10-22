package com.light.hexo.common.component.file;
import com.light.hexo.common.exception.GlobalException;
import org.apache.commons.lang3.StringUtils;

/**
 * @Author MoonlightL
 * @ClassName: FileService
 * @ProjectName hexo-boot
 * @Description: 文件 Service
 * @DateTime 2020/9/10 16:26
 */
public interface FileService {

    /**
     * 协议
     */
    String PROTOCOL = "http";

    /**
     * 上传文件
     * @param fileRequest
     * @return
     * @throws GlobalException
     */
    FileResponse upload(FileRequest fileRequest) throws GlobalException;

    /**
     * 下载文件
     * @param fileRequest
     * @return
     * @throws GlobalException
     */
    FileResponse download(FileRequest fileRequest) throws GlobalException;

    /**
     * 删除文件
     * @param fileRequest
     * @return
     * @throws GlobalException
     */
    FileResponse remove(FileRequest fileRequest) throws GlobalException;

    /**
     * 转换 url
     * @param url
     * @return
     */
    default String parseUrl(String url) {

        if (StringUtils.isBlank(url)) {
            return null;
        }

        if (!url.startsWith(PROTOCOL)) {
            url = PROTOCOL + "://" + url;
        }

        return url;
    }

    /**
     * 文件管理方式，参考：FileManageEnum 枚举
     * @return
     */
    int getCode();
}
