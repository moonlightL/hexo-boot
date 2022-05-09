package com.light.hexo.common.component.file;

import com.light.hexo.common.component.CommonServiceFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author MoonlightL
 * @ClassName: FileServiceFactory
 * @ProjectName hexo-boot
 * @Description: FileService 工厂
 * @DateTime 2020/9/10 16:31
 */
@Component
@Slf4j
public class FileServiceFactory extends CommonServiceFactory<FileService> {

    private static final Map<String, FileService> SERVICE_MAP = new HashMap<>();

    @Override
    protected Map<String, FileService> getServiceMap() {
        return SERVICE_MAP;
    }
}