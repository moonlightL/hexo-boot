package com.light.hexo.core.admin.service.impl;

import com.light.hexo.common.base.BaseServiceImpl;
import com.light.hexo.common.constant.ConfigEnum;
import com.light.hexo.mapper.mapper.BackupMapper;
import com.light.hexo.mapper.base.BaseMapper;
import com.light.hexo.mapper.model.Backup;
import com.light.hexo.core.admin.service.BackupService;
import com.light.hexo.core.admin.service.ConfigService;
import com.light.hexo.common.base.BaseRequest;
import com.light.hexo.common.exception.GlobalException;
import com.light.hexo.common.request.BackupRequest;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author MoonlightL
 * @ClassName: BackupServiceImpl
 * @ProjectName hexo-boot
 * @Description: 备份 Service 实现
 * @DateTime 2020/9/8 18:43
 */
@Service
public class BackupServiceImpl extends BaseServiceImpl<Backup> implements BackupService {

    @Autowired
    private BackupMapper backupMapper;

    @Autowired
    private ConfigService configService;

    @Override
    public BaseMapper<Backup> getBaseMapper() {
        return this.backupMapper;
    }

    @Override
    protected Example getExample(BaseRequest request) {
        BackupRequest backupRequest = (BackupRequest) request;
        Example example = new Example(Backup.class);
        Example.Criteria criteria = example.createCriteria();

        String name = backupRequest.getName();
        if (StringUtils.isNotBlank(name)) {
            criteria.andLike("name", "%" + name + "%");
        }

        example.orderBy("id").desc();
        return example;
    }

    @Override
    public Backup saveBackup(String sqlData) throws GlobalException {

        Map<String, String> configMap = this.configService.getConfigMap();
        String dirPath = configMap.get(ConfigEnum.BACKUP_DIR.getName());
        if (StringUtils.isNotBlank(dirPath)) {
            File dir = new File(dirPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
        }

        String filename = String.format("backup_%d.sql", System.currentTimeMillis());
        File file = new File(dirPath, filename);

        Backup backup = new Backup();
        backup.setName(file.getName())
              .setFilePath(file.getAbsolutePath())
              .setFileSize((long) sqlData.length());

        try (OutputStream output = new FileOutputStream(file)) {
            // 将 SQL 内容保存到本地
            IOUtils.write(sqlData.getBytes(), output);
            super.saveModel(backup);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return backup;
    }

    @Override
    public void removeBackupBatch(List<String> idStrList) throws GlobalException {
        List<Long> idList = idStrList.stream().map(Long::valueOf).collect(Collectors.toList());
        Example example = new Example(Backup.class);
        example.createCriteria().andIn("id", idList);

        List<Backup> backupList = this.getBaseMapper().selectByExample(example);
        if (CollectionUtils.isEmpty(backupList)) {
            return;
        }

        // 删除本地文件
        for (Backup backup : backupList) {
            File file = new File(backup.getFilePath());
            if (file.exists()) {
                file.delete();
            }
        }

        // 删除记录
        this.getBaseMapper().deleteByExample(example);
    }

    @Override
    public void saveConfig(Map<String, String> configMap) throws GlobalException {
        this.configService.saveConfig(configMap);
    }
}
