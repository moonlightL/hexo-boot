package com.light.hexo.component;

import cn.hutool.core.util.RandomUtil;
import com.light.hexo.common.component.file.FileRequest;
import com.light.hexo.common.component.file.FileResponse;
import com.light.hexo.common.component.file.FileService;
import com.light.hexo.common.component.file.FileServiceFactory;
import com.light.hexo.common.constant.HexoConstant;
import com.light.hexo.common.exception.GlobalException;
import com.light.hexo.common.util.ExceptionUtil;
import com.light.hexo.common.util.IpUtil;
import com.light.hexo.config.BlogProperty;
import com.light.hexo.mapper.model.Attachment;
import com.light.hexo.core.admin.service.AttachmentService;
import com.light.hexo.core.admin.service.ConfigService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @Author MoonlightL
 * @ClassName: DefaultFileService
 * @ProjectName hexo-boot
 * @Description: 默认文件 Servcie
 * @DateTime 2020/9/11 9:36
 */
@Component
@Slf4j
public class DefaultFileService {

    @Autowired
    private FileServiceFactory fileServiceFactory;

    @Autowired
    private ConfigService configService;

    @Autowired
    private BlogProperty blogProperty;

    @Autowired
    private Environment environment;

    @Autowired
    private AttachmentService attachmentService;

    private static final Map<String, String> THUMBNAIL_URL_MAP;

    static {
        THUMBNAIL_URL_MAP = new HashMap<>();
        THUMBNAIL_URL_MAP.put("doc", "/admin/assets/custom/images/doc.jpg");
        THUMBNAIL_URL_MAP.put("docx", "/admin/assets/custom/images/doc.jpg");
        THUMBNAIL_URL_MAP.put("xls", "/admin/assets/custom/images/xls.jpg");
        THUMBNAIL_URL_MAP.put("xlsx", "/admin/assets/custom/images/xls.jpg");
        THUMBNAIL_URL_MAP.put("ppt", "/admin/assets/custom/images/pptx.jpg");
        THUMBNAIL_URL_MAP.put("pptx", "/admin/assets/custom/images/pptx.jpg");
        THUMBNAIL_URL_MAP.put("pdf", "/admin/assets/custom/images/pdf.jpg");
        THUMBNAIL_URL_MAP.put("txt", "/admin/assets/custom/images/txt.jpg");
        THUMBNAIL_URL_MAP.put("sql", "/admin/assets/custom/images/sql.jpg");
        THUMBNAIL_URL_MAP.put("md", "/admin/assets/custom/images/markdown.jpg");
    }

    @SneakyThrows
    public FileResponse upload(FileRequest fileRequest) {

        FileService fileService = this.getFileService();

        CompletableFuture<FileResponse> secondFuture = CompletableFuture.supplyAsync(() -> {
            long start = System.currentTimeMillis();
            FileResponse fileResponse = fileService.upload(fileRequest);
            log.info("========== DefaultFileService 上传至远程图床耗时: {} ms============", (System.currentTimeMillis() - start));
            return fileResponse;
        });

        FileResponse fileResponse;

        try {
            fileResponse = secondFuture.get(10, TimeUnit.SECONDS);
        } catch (Exception e) {
            fileResponse = new FileResponse();
            if (e instanceof TimeoutException) {
                fileResponse.setSuccess(true);
                fileResponse.setErrorMsg("上传等待超时，请稍等30秒后刷新页面再查看上传文件");
            }
            e.printStackTrace();
        }

        fileResponse.setOriginalName(fileRequest.getOriginalName());
        fileResponse.setFilename(fileRequest.getFilename());
        fileResponse.setUrl(fileService.getFileUrl(fileRequest.getFilename()));
        fileResponse.setPath(fileService.getLocalPath(fileRequest.getFilename()));

        if (fileResponse.isSuccess()) {
            Attachment attachment = new Attachment();
            attachment.setFilename(fileRequest.getFilename())
                      .setOriginalName(fileRequest.getOriginalName())
                      .setContentType(fileRequest.getContentType())
                      .setFileType(this.checkFileType(attachment.getContentType()).getType())
                      .setFileUrl(fileResponse.getUrl())
                      .setFilePath(fileResponse.getPath())
                      .setFileSize(fileRequest.getFileSize())
                      .setPosition(Integer.valueOf(this.configService.getConfigValue(com.light.hexo.constant.ConfigEnum.MANAGE_MODE.getName())));

            if (THUMBNAIL_URL_MAP.containsKey(fileRequest.getExtension())) {
                attachment.setThumbnailUrl(THUMBNAIL_URL_MAP.get(fileRequest.getExtension()));
            } else {
                if (attachment.getContentType().startsWith(com.light.hexo.constant.FileTypeEnum.VIDEO.getCode())) {

                    String coverBase64 = fileRequest.getCoverBase64();
                    if (StringUtils.isNotBlank(coverBase64)) {
                        Base64.Decoder decoder = Base64.getDecoder();
                        coverBase64 = coverBase64.substring(coverBase64.indexOf(",", 1) + 1);
                        byte[] data = decoder.decode(coverBase64);
                        for (int i = 0; i < data.length; ++i) {
                            if (data[i] < 0) {
                                data[i] += 256;
                            }
                        }
                        String filePath = this.configService.getConfigValue(com.light.hexo.constant.ConfigEnum.LOCAL_FILE_PATH.getName());
                        String localFilePath = StringUtils.isNotBlank(filePath) ? filePath  + File.separator : this.blogProperty.getAttachmentDir();
                        String coverName = FilenameUtils.getBaseName(fileRequest.getOriginalName()) + "_" + RandomUtil.randomNumbers(6) + ".jpg";
                        String coverPath = localFilePath + "/cover/" + coverName;
                        File target = new File(coverPath);

                        IOUtils.write(data, new FileOutputStream(target));

                        String blogPage = this.configService.getConfigValue(com.light.hexo.constant.ConfigEnum.HOME_PAGE.getName());
                        String coverUrl = (StringUtils.isNotBlank(blogPage) ? blogPage : "http://" + IpUtil.getHostIp() + ":" + this.environment.getProperty("server.port")) + "/cover/" + coverName;
                        attachment.setThumbnailUrl(coverUrl);
                    } else {
                        attachment.setThumbnailUrl(HexoConstant.DEFAULT_VIDEO_COVER);
                    }

                } else {
                    attachment.setThumbnailUrl(attachment.getFileUrl());
                }
            }

            this.attachmentService.saveModel(attachment);

            fileResponse.setCoverUrl(attachment.getThumbnailUrl());
        }

        return fileResponse;
    }

    private com.light.hexo.constant.FileTypeEnum checkFileType(String contentType) {
        if (contentType.startsWith(com.light.hexo.constant.FileTypeEnum.IMAGE.getCode())) {
            return com.light.hexo.constant.FileTypeEnum.IMAGE;
        } else if (contentType.startsWith(com.light.hexo.constant.FileTypeEnum.VIDEO.getCode())) {
            return com.light.hexo.constant.FileTypeEnum.VIDEO;
        }
        return com.light.hexo.constant.FileTypeEnum.OTHER;
    }

    public FileResponse download (FileRequest fileRequest, Integer position) throws GlobalException {
        if (!this.getManageMode().equals(position.toString())) {
            ExceptionUtil.throwExToPage(com.light.hexo.constant.HexoExceptionEnum.ERROR_ATTACHMENT_NOT_POSITION);
        }

        return this.getFileService().download(fileRequest);
    }


    public FileResponse remove(FileRequest fileRequest, Integer position) throws GlobalException {
        if (!this.getManageMode().equals(position.toString())) {
            ExceptionUtil.throwEx(com.light.hexo.constant.HexoExceptionEnum.ERROR_ATTACHMENT_NOT_POSITION);
        }

        return this.getFileService().remove(fileRequest);
    }


    /**
     * 获取 FileService 实现
     * @return
     */
    private FileService getFileService() {
        String configValue = this.getManageMode();
        return this.fileServiceFactory.getInstance(Integer.valueOf(configValue));
    }

    public String getManageMode() {
        return this.configService.getConfigValue(com.light.hexo.constant.ConfigEnum.MANAGE_MODE.getName());
    }
}
