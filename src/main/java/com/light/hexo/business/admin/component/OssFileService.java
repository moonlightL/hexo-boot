package com.light.hexo.business.admin.component;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.OSSObject;
import com.light.hexo.business.admin.constant.ConfigEnum;
import com.light.hexo.business.admin.constant.HexoExceptionEnum;
import com.light.hexo.business.admin.service.ConfigService;
import com.light.hexo.common.component.file.FileManageEnum;
import com.light.hexo.common.component.file.FileRequest;
import com.light.hexo.common.component.file.FileResponse;
import com.light.hexo.common.component.file.FileService;
import com.light.hexo.common.exception.GlobalException;
import com.light.hexo.common.util.ExceptionUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.util.Map;

/**
 * @Author MoonlightL
 * @ClassName: OssFileService
 * @ProjectName hexo-boot
 * @Description: OSS 文件管理，参考：https://help.aliyun.com/document_detail/84781.html?spm=a2c4g.11186623.6.834.4ae66328XPkGZq
 * @DateTime 2020/9/10 17:04
 */
@Component
@Slf4j
public class OssFileService implements FileService {

    @Autowired
    private ConfigService configService;

    @Override
    public FileResponse upload(FileRequest fileRequest) throws GlobalException {
        FileResponse fileResponse = new FileResponse();

        String fileName = fileRequest.getFilename();
        byte[] data = fileRequest.getData();

        OSS ossClient = null;

        try {
            ossClient = this.buildOssClient();
            ossClient.putObject(this.getBucket(), fileName, new ByteArrayInputStream(data));

            fileResponse.setSuccess(true).setUrl(this.parseUrl(this.getBucket() + "." + this.getEndpoint() + "/" + fileName));

        } catch (Exception e) {
            log.error("========【OSS 管理】文件 fileName: {} 文件上传失败=============", fileName);
            e.printStackTrace();
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }

        return fileResponse;
    }

    @Override
    public FileResponse download(FileRequest fileRequest) throws GlobalException {
        FileResponse fileResponse = new FileResponse();

        String fileName = fileRequest.getFilename();

        OSS ossClient = null;

        try {
            ossClient = this.buildOssClient();
            OSSObject ossObject = ossClient.getObject(this.getBucket(), fileName);
            byte[] data = IOUtils.toByteArray(ossObject.getObjectContent());
            fileResponse.setSuccess(true).setData(data);

        } catch (Exception e) {
            log.error("========【OSS 管理】文件 fileName: {} 文件下载失败=============", fileName);
            e.printStackTrace();
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }

        return fileResponse;
    }

    @Override
    public FileResponse remove(FileRequest fileRequest) throws GlobalException {
        FileResponse fileResponse = new FileResponse();

        String fileName = fileRequest.getFilename();

        OSS ossClient = null;
        try {
            ossClient = this.buildOssClient();
            ossClient.deleteObject(this.getBucket(), fileName);

            fileResponse.setSuccess(true);

        } catch (GlobalException e) {
            throw e;

        } catch (Exception e) {
            log.error("========【OSS 管理】文件 fileName: {} 文件删除失败=============", fileName);
            e.printStackTrace();
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }

        return fileResponse;
    }

    @Override
    public int getCode() {
        return FileManageEnum.OSS.getCode();
    }

    /**
     * 构建 oss 客户端
     * @return
     * @throws GlobalException
     */
    private OSS buildOssClient() throws GlobalException {
        Map<String, String> configMap = this.configService.getConfigMap();
        // Region 请按实际情况填写
        String endpoint = configMap.get(ConfigEnum.OSS_ENDPOINT.getName());
        // 建议使用RAM账号进行API访问或日常运维，访问 https://ram.console.aliyun.com 创建RAM账号
        String accessKeyId = configMap.get(ConfigEnum.OSS_ACCESS_KEY.getName());
        String accessKeySecret = configMap.get(ConfigEnum.OSS_SECRET_KEY.getName());

        if (StringUtils.isBlank(endpoint)
                || StringUtils.isBlank(accessKeyId)
                || StringUtils.isBlank(accessKeySecret)) {
            ExceptionUtil.throwEx(HexoExceptionEnum.ERROR_OSS_CONFIG_IS_EMPTY);
        }

        // 创建OSSClient实例
        OSS ossClient = new OSSClientBuilder().build("http://" + endpoint, accessKeyId, accessKeySecret);

        return ossClient;
    }

    /**
     * 获取 endpoint
     * @return
     * @throws GlobalException
     */
    private String getEndpoint() throws GlobalException {
        Map<String, String> configMap = this.configService.getConfigMap();
        String endpoint = configMap.get(ConfigEnum.OSS_ENDPOINT.getName());
        if (StringUtils.isBlank(endpoint)) {
            ExceptionUtil.throwEx(HexoExceptionEnum.ERROR_OSS_CONFIG_IS_EMPTY);
        }

        return endpoint;
    }

    /**
     * 获取 bucket
     * @return
     */
    private String getBucket() throws GlobalException {
        Map<String, String> configMap = this.configService.getConfigMap();
        String bucket = configMap.get(ConfigEnum.OSS_BUCKET.getName());
        if (StringUtils.isBlank(bucket)) {
            ExceptionUtil.throwEx(HexoExceptionEnum.ERROR_OSS_CONFIG_IS_EMPTY);
        }

        return bucket;
    }
}
