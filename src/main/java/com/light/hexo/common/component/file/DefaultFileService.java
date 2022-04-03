package com.light.hexo.common.component.file;

import com.light.hexo.business.admin.constant.ConfigEnum;
import com.light.hexo.business.admin.constant.FileTypeEnum;
import com.light.hexo.business.admin.constant.HexoExceptionEnum;
import com.light.hexo.business.admin.model.Attachment;
import com.light.hexo.business.admin.service.AttachmentService;
import com.light.hexo.business.admin.service.ConfigService;
import com.light.hexo.common.exception.GlobalException;
import com.light.hexo.common.util.ExceptionUtil;
import com.light.hexo.common.util.VideoUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
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
    private AttachmentService attachmentService;

    @Autowired
    private VideoUtil videoUtil;

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

        CompletableFuture<String> firstFuture = CompletableFuture.supplyAsync(() -> {
            long start = System.currentTimeMillis();
            // 临时拷贝
            File tmpDir = new File(System.getProperty("java.io.tmpdir"));
            File dest = new File(tmpDir, fileRequest.getFilename());
            try {
                FileUtils.writeByteArrayToFile(dest, fileRequest.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
            log.info("========== DefaultFileService 拷贝临时文件耗时: {} ms============", (System.currentTimeMillis() - start));
            return dest;
        }).thenApplyAsync((file) -> {
            long start = System.currentTimeMillis();
            String coverUrl = this.videoUtil.createCover(FilenameUtils.getBaseName(fileRequest.getOriginalName()), file.getAbsolutePath());
            FileUtils.deleteQuietly(file);
            log.info("========== DefaultFileService 生成封面耗时: {} ms============", (System.currentTimeMillis() - start));
            return coverUrl;
        });

        CompletableFuture<FileResponse> secondFuture = CompletableFuture.supplyAsync(() -> {
            long start = System.currentTimeMillis();
            FileResponse fileResponse = fileService.upload(fileRequest);
            log.info("========== DefaultFileService 上传至远程图床耗时: {} ms============", (System.currentTimeMillis() - start));
            return fileResponse;
        });

        String coverUrl = firstFuture.get();

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
                      .setPosition(Integer.valueOf(this.configService.getConfigValue(ConfigEnum.MANAGE_MODE.getName())));

            if (THUMBNAIL_URL_MAP.containsKey(fileRequest.getExtension())) {
                attachment.setThumbnailUrl(THUMBNAIL_URL_MAP.get(fileRequest.getExtension()));
            } else {
                if (attachment.getContentType().startsWith(FileTypeEnum.VIDEO.getCode())) {
                    attachment.setThumbnailUrl(coverUrl);
                } else {
                    attachment.setThumbnailUrl(attachment.getFileUrl());
                }
            }

            this.attachmentService.saveModel(attachment);

            fileResponse.setCoverUrl(attachment.getThumbnailUrl());
        }

        return fileResponse;
    }

    private FileTypeEnum checkFileType(String contentType) {
        if (contentType.startsWith(FileTypeEnum.IMAGE.getCode())) {
            return FileTypeEnum.IMAGE;
        } else if (contentType.startsWith(FileTypeEnum.VIDEO.getCode())) {
            return FileTypeEnum.VIDEO;
        }
        return FileTypeEnum.OTHER;
    }

    public FileResponse download (FileRequest fileRequest, Integer position) throws GlobalException {
        if (!this.getManageMode().equals(position.toString())) {
            ExceptionUtil.throwExToPage(HexoExceptionEnum.ERROR_ATTACHMENT_NOT_POSITION);
        }

        return this.getFileService().download(fileRequest);
    }


    public FileResponse remove(FileRequest fileRequest, Integer position) throws GlobalException {
        if (!this.getManageMode().equals(position.toString())) {
            ExceptionUtil.throwEx(HexoExceptionEnum.ERROR_ATTACHMENT_NOT_POSITION);
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
        return this.configService.getConfigValue(ConfigEnum.MANAGE_MODE.getName());
    }
}
