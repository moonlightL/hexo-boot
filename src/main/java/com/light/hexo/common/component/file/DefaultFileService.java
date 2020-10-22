package com.light.hexo.common.component.file;

import com.light.hexo.business.admin.constant.ConfigEnum;
import com.light.hexo.business.admin.model.Attachment;
import com.light.hexo.business.admin.service.AttachmentService;
import com.light.hexo.business.admin.service.ConfigService;
import com.light.hexo.common.exception.GlobalException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author MoonlightL
 * @ClassName: DefaultFileService
 * @ProjectName hexo-boot
 * @Description: 默认文件 Servcie
 * @DateTime 2020/9/11 9:36
 */
@Component
public class DefaultFileService {

    @Autowired
    private FileServiceFactory fileServiceFactory;

    @Autowired
    private ConfigService configService;

    @Autowired
    private AttachmentService attachmentService;

    private static final Map<String, String> THUMBNAIL_URL_MAP;

    static {
        THUMBNAIL_URL_MAP = new HashMap<>();
        THUMBNAIL_URL_MAP.put("doc", "/admin/assets/custom/images/doc.jpg");
        THUMBNAIL_URL_MAP.put("docx", "/admin/assets/custom/images/doc.jpg");
        THUMBNAIL_URL_MAP.put("xls.jpg", "/admin/assets/custom/images/xls.jpg");
        THUMBNAIL_URL_MAP.put("xlx.jpg", "/admin/assets/custom/images/xls.jpg");
        THUMBNAIL_URL_MAP.put("ppt", "/admin/assets/custom/images/pptx.jpg");
        THUMBNAIL_URL_MAP.put("pptx", "/admin/assets/custom/images/pptx.jpg");
        THUMBNAIL_URL_MAP.put("pdf", "/admin/assets/custom/images/pdf.jpg");
        THUMBNAIL_URL_MAP.put("txt", "/admin/assets/custom/images/txt.jpg");
        THUMBNAIL_URL_MAP.put("sql", "/admin/assets/custom/images/sql.jpg");
    }

    public FileResponse upload(FileRequest fileRequest) throws GlobalException {

        FileResponse fileResponse = this.getFileService().upload(fileRequest);
        if (fileResponse.getSuccess()) {
            Attachment attachment = new Attachment();
            attachment.setFilename(fileRequest.getFilename())
                    .setOriginalName(fileRequest.getOriginalName())
                    .setContentType(fileRequest.getContentType())
                    .setFileUrl(fileResponse.getUrl())
                    .setFilePath(fileResponse.getPath())
                    .setFileSize(fileRequest.getFileSize())
                    .setFileKey(fileResponse.getKey())
                    .setPosition(Integer.valueOf(this.configService.getConfigValue(ConfigEnum.MANAGE_MODE.getName())));

            if (THUMBNAIL_URL_MAP.containsKey(fileRequest.getExtension())) {
                attachment.setThumbnailUrl(THUMBNAIL_URL_MAP.get(fileRequest.getExtension()));
            } else {
                attachment.setThumbnailUrl(attachment.getFileUrl());
            }

            this.attachmentService.saveModel(attachment);
        }

        return fileResponse;
    }

    public FileResponse remove(FileRequest fileRequest) throws GlobalException {
        return this.getFileService().remove(fileRequest);
    }


    /**
     * 获取 FileService 实现
     * @return
     */
    private FileService getFileService() {
        String configValue = this.configService.getConfigValue(ConfigEnum.MANAGE_MODE.getName());
        return this.fileServiceFactory.getInstance(Integer.valueOf(configValue));
    }
}
