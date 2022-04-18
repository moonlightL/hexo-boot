package com.light.hexo.core.admin.component;

import com.light.hexo.core.admin.config.BlogConfig;
import com.light.hexo.core.admin.constant.ConfigEnum;
import com.light.hexo.core.admin.constant.HexoExceptionEnum;
import com.light.hexo.core.admin.service.ConfigService;
import com.light.hexo.common.component.file.FileManageEnum;
import com.light.hexo.common.component.file.FileRequest;
import com.light.hexo.common.component.file.FileResponse;
import com.light.hexo.common.component.file.FileService;
import com.light.hexo.common.exception.GlobalException;
import com.light.hexo.common.util.ExceptionUtil;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.http.HttpProtocol;
import com.qcloud.cos.model.GetObjectRequest;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.UploadResult;
import com.qcloud.cos.region.Region;
import com.qcloud.cos.transfer.Download;
import com.qcloud.cos.transfer.TransferManager;
import com.qcloud.cos.transfer.TransferManagerConfiguration;
import com.qcloud.cos.transfer.Upload;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Author MoonlightL
 * @ClassName: CosFileService
 * @ProjectName hexo-boot
 * @Description: COS 对象存储，参考 https://cloud.tencent.com/document/product/436/65935
 * @DateTime 2022/4/2, 0002 16:33
 */
@Component
@Slf4j
public class CosFileService implements FileService {

    @Autowired
    private ConfigService configService;

    @Autowired
    private BlogConfig blogConfig;

    @Override
    public FileResponse upload(FileRequest fileRequest) throws GlobalException {

        FileResponse fileResponse = new FileResponse();

        InputStream inputStream = fileRequest.getInputStream();
        String filename = fileRequest.getFilename();

        TransferManager transferManager = this.createTransferManager();

        String bucketName = this.getBucket();

        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, filename, inputStream, new ObjectMetadata());

        try {
            Upload upload = transferManager.upload(putObjectRequest);
            UploadResult uploadResult = upload.waitForUploadResult();
            fileResponse.setSuccess(true).setUrl(this.getFileUrl(filename));

        } catch (Exception e) {
            log.error("========【OSS 管理】文件 fileName: {} 文件上传失败=============", filename);
            e.printStackTrace();
        } finally {
            if (transferManager != null) {
                transferManager.shutdownNow(true);
            }
        }

        return fileResponse;
    }

    @Override
    public FileResponse download(FileRequest fileRequest) throws GlobalException {
        FileResponse fileResponse = new FileResponse();
        TransferManager transferManager = this.createTransferManager();

        String bucketName = this.getBucket();
        String filename = fileRequest.getFilename();
        GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName, filename);

        try {
            File target = new File(this.getUploadDir() + fileRequest.getFilename());
            Download download = transferManager.download(getObjectRequest, target);
            download.waitForCompletion();

            byte[] data = FileUtils.readFileToByteArray(target);
            fileResponse.setSuccess(true).setData(data);

        } catch (Exception e) {
            log.error("========【COS 管理】文件 filename: {} 文件下载失败=============", filename);
            e.printStackTrace();
        } finally {
            if (transferManager != null) {
                transferManager.shutdownNow(true);
            }
        }
        return fileResponse;
    }

    @Override
    public FileResponse remove(FileRequest fileRequest) throws GlobalException {

        FileResponse fileResponse = new FileResponse();
        String fileKey = fileRequest.getFilename();

        COSClient cosClient = null;
        try {
            cosClient = this.createCOSClient();
            String bucket = this.getBucket();
            cosClient.deleteObject(bucket, fileKey);
            fileResponse.setSuccess(true);
            return fileResponse;
        } catch (Exception e) {
            log.error("========【COS 管理】文件 fileName: {} 文件删除失败=============", fileRequest.getFileKey());
            e.printStackTrace();
        } finally {
            if (cosClient != null) {
                cosClient.shutdown();
            }
        }
        return fileResponse;
    }

    @Override
    public String getFileUrl(String filename) throws GlobalException {
        return this.parseUrl(this.getDomain() + "/" + filename);
    }

    @Override
    public int getCode() {
        return FileManageEnum.COS.getCode();
    }

    private String getUploadDir() {
        String uploadDir = this.configService.getConfigValue(ConfigEnum.LOCAL_FILE_PATH.getName());
        return StringUtils.isBlank(uploadDir) ? this.blogConfig.getAttachmentDir() : uploadDir;
    }

    private String getDomain() throws GlobalException {
        Map<String, String> configMap = this.configService.getConfigMap();

        String bucket = configMap.get(ConfigEnum.COS_DOMAIN.getName());
        if (StringUtils.isBlank(bucket)) {
            ExceptionUtil.throwEx(HexoExceptionEnum.ERROR_QN_CONFIG_IS_EMPTY);
        }

        return bucket;
    }

    private String getBucket() throws GlobalException {
        Map<String, String> configMap = this.configService.getConfigMap();

        String bucket = configMap.get(ConfigEnum.COS_BUCKET.getName());
        if (StringUtils.isBlank(bucket)) {
            ExceptionUtil.throwEx(HexoExceptionEnum.ERROR_QN_CONFIG_IS_EMPTY);
        }

        return bucket;
    }

    private TransferManager createTransferManager() {
        COSClient cosClient = createCOSClient();

        ExecutorService threadPool = Executors.newFixedThreadPool(32);
        TransferManager transferManager = new TransferManager(cosClient, threadPool);

        TransferManagerConfiguration transferManagerConfiguration = new TransferManagerConfiguration();
        transferManagerConfiguration.setMultipartUploadThreshold(5 * 1024 * 1024);
        transferManagerConfiguration.setMinimumUploadPartSize(1 * 1024 * 1024);
        transferManager.setConfiguration(transferManagerConfiguration);

        return transferManager;
    }

    private static final Map<String, String> REGION_MAP = new HashMap<>();

    static {
        // 北京一区（已售罄）
        REGION_MAP.put("0", "ap-beijing-1");
        // 北京
        REGION_MAP.put("1", "ap-beijing");
        // 南京
        REGION_MAP.put("2", "ap-nanjing");
        // 上海
        REGION_MAP.put("3", "ap-shanghai");
        // 广州
        REGION_MAP.put("4", "ap-guangzhou");
        // 成都
        REGION_MAP.put("5", "ap-chengdu");
        // 重庆
        REGION_MAP.put("6", "ap-chongqing");
        // 深圳金融
        REGION_MAP.put("7", "ap-shenzhen-fsi");
        // 上海金融
        REGION_MAP.put("8", "ap-shanghai-fsi");
        // 北京金融
        REGION_MAP.put("9", "ap-beijing-fsi");
    }

    private COSClient createCOSClient() {

        Map<String, String> configMap = this.configService.getConfigMap();

        // 设置用户身份信息。
        String secretId = configMap.get(ConfigEnum.COS_SECRET_ID.getName());
        String secretKey = configMap.get(ConfigEnum.COS_SECRET_KEY.getName());

        COSCredentials cred = new BasicCOSCredentials(secretId, secretKey);

        ClientConfig clientConfig = new ClientConfig();

        // 设置 bucket 的地域
        String regionKey = configMap.get(ConfigEnum.COS_REGION.getName());
        // COS_REGION 请参照 https://cloud.tencent.com/document/product/436/6224
        clientConfig.setRegion(new Region(REGION_MAP.get(regionKey)));

        // 设置请求协议, http 或者 https
        clientConfig.setHttpProtocol(HttpProtocol.https);

        // 生成 cos 客户端。
        return new COSClient(cred, clientConfig);
    }
}
