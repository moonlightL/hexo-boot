package com.light.hexo.core.admin.service.impl;

import cn.hutool.core.util.ZipUtil;
import com.light.hexo.common.base.BaseRequest;
import com.light.hexo.common.base.BaseServiceImpl;
import com.light.hexo.common.constant.HexoExceptionEnum;
import com.light.hexo.common.exception.GlobalException;
import com.light.hexo.common.plugin.BasePlugin;
import com.light.hexo.common.plugin.HexoBootPluginManager;
import com.light.hexo.common.request.PluginRequest;
import com.light.hexo.common.util.DateUtil;
import com.light.hexo.common.util.ExceptionUtil;
import com.light.hexo.core.admin.service.SysPluginService;
import com.light.hexo.mapper.base.BaseMapper;
import com.light.hexo.mapper.mapper.SysPluginMapper;
import com.light.hexo.mapper.model.SysPlugin;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.pf4j.PluginDescriptor;
import org.pf4j.PluginState;
import org.pf4j.PluginWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @Author MoonlightL
 * @ClassName: SysPluginServiceImpl
 * @ProjectName hexo-boot
 * @Description: 插件 Service 实现
 * @DateTime 2022/4/13, 0013 10:34
 */
@Service
@Slf4j
public class SysPluginServiceImpl extends BaseServiceImpl<SysPlugin> implements SysPluginService {

    @Autowired
    private SysPluginMapper pluginMapper;

    @Autowired
    private HexoBootPluginManager pluginManager;

    @Override
    public BaseMapper<SysPlugin> getBaseMapper() {
        return this.pluginMapper;
    }

    @Override
    protected Example getExample(BaseRequest request) {
        PluginRequest pluginRequest = (PluginRequest) request;
        Example example = new Example(SysPlugin.class);

        Example.Criteria criteria = example.createCriteria();

        String pluginId = pluginRequest.getPluginId();
        if (StringUtils.isNotBlank(pluginId)) {
            criteria.andLike("pluginId", pluginId.trim() + "%");
        }

        return example;
    }

    @Override
    public int removeModel(Serializable id) throws GlobalException {
        return super.removeModel(id);
    }

    @Override
    public void installPlugin(String originalFilename, InputStream inputStream) throws GlobalException {

        String originName = FilenameUtils.getBaseName(originalFilename);
        Example example = new Example(SysPlugin.class);
        example.createCriteria().andEqualTo("originName", originName);
        SysPlugin dbPlugin = this.pluginMapper.selectOneByExample(example);
        if (dbPlugin != null) {
            ExceptionUtil.throwEx(HexoExceptionEnum.ERROR_PLUGIN_INSTALLED);
        }

        Path pluginsRoot = this.pluginManager.getPluginsRoot();
        String parentDirPath = pluginsRoot.toString();
        File unzipPluginCog = ZipUtil.unzip(inputStream, new File(parentDirPath), Charset.defaultCharset());
        File jarFile = new File(unzipPluginCog.getAbsolutePath(), originName + ".jar");
        if (!jarFile.exists()) {
            ExceptionUtil.throwEx(HexoExceptionEnum.ERROR_PLUGIN_INVALID);
        }

        String filePath = jarFile.getAbsolutePath();
        String pluginId = this.pluginManager.loadPlugin(Paths.get(filePath));
        PluginWrapper pluginWrapper = this.pluginManager.getPlugin(pluginId);
        PluginDescriptor descriptor = pluginWrapper.getDescriptor();

        try {
            PluginState pluginState = this.pluginManager.startPlugin(pluginId, filePath);
            SysPlugin plugin = new SysPlugin();
            plugin.setPluginId(pluginId)
                  .setOriginName(originName)
                  .setState(pluginState.toString().equals(PluginState.STARTED.toString()))
                  .setRemark(descriptor.getPluginDescription())
                  .setVersion(descriptor.getVersion())
                  .setAuthor(descriptor.getProvider())
                  .setFilePath(filePath)
                  .setConfigUrl(((BasePlugin) pluginWrapper.getPlugin()).getConfigUrl())
                  .setCreateTime(LocalDateTime.now())
                  .setUpdateTime(plugin.getCreateTime());
            this.pluginMapper.insert(plugin);

        } catch (Exception e) {
            this.pluginManager.unloadPlugin(pluginId);
        }
    }

    @Override
    public void updatePlugin(SysPlugin plugin) throws GlobalException {

        SysPlugin dbPlugin = super.findById(plugin.getId());
        if (dbPlugin == null) {
            ExceptionUtil.throwEx(HexoExceptionEnum.ERROR_PLUGIN_NOT_EXIST);
        }

        if (dbPlugin.getState().equals(plugin.getState())) {
            return;
        }

        String pluginId = dbPlugin.getPluginId();

        if (plugin.getState()) {
            this.pluginManager.startPlugin(pluginId, dbPlugin.getFilePath());
        } else {
            this.pluginManager.stopPlugin(pluginId);
        }

        super.updateModel(plugin);
    }

    @Override
    public void uninstallPlugin(Integer id) throws GlobalException {

        SysPlugin dbPlugin = super.findById(id);
        if (dbPlugin == null) {
            ExceptionUtil.throwEx(HexoExceptionEnum.ERROR_PLUGIN_NOT_EXIST);
        }

        String pluginId = dbPlugin.getPluginId();

        File pluginFile = new File(dbPlugin.getFilePath());
        if (pluginFile.exists()) {
            File parentFile = pluginFile.getParentFile();
            File bakFileDir = new File(parentFile.getParentFile().getAbsolutePath(), "plugins-bak");
            if (!bakFileDir.exists()) {
                bakFileDir.mkdirs();
            }

            try {
                String dateTimeStr = DateUtil.ldtToStr(LocalDateTime.now(), DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
                File bakFile = new File(bakFileDir.getAbsolutePath(), FilenameUtils.getBaseName(pluginFile.getName()) + "-" + dateTimeStr + ".jar");
                FileUtils.copyFile(pluginFile, bakFile);
            } catch (IOException e) {
                log.error("=========== uninstallPlugin 备份失败 plugin-id: {} =================", pluginId);
            }

            if (this.pluginManager.checkPlugin(pluginId)) {
                try {
                    this.pluginManager.deletePlugin(pluginId);
                    super.removeModel(id);
                } catch (Exception e) {
                    log.error("=========== uninstallPlugin 删除插件失败 plugin-id: {}, error: {}=================", pluginId, e);
                    this.deletePluginFileError(dbPlugin);
                }
            } else {
                boolean deleteQuietly = false;
                int tryCount = 0;
                while (!deleteQuietly && tryCount++ < 10) {
                    System.gc();
                    deleteQuietly = FileUtils.deleteQuietly(pluginFile);
                }

                if (!deleteQuietly) {
                    this.deletePluginFileError(dbPlugin);
                }
                super.removeModel(id);
            }

            System.gc();
        }
    }

    @Override
    public boolean checkPlugin(String pluginId) throws GlobalException {
        Example example = new Example(SysPlugin.class);
        example.createCriteria().andEqualTo("pluginId", pluginId);
        SysPlugin dbPlugin = this.pluginMapper.selectOneByExample(example);
        return dbPlugin != null && dbPlugin.getState();
    }

    private void deletePluginFileError(SysPlugin sysPlugin) {
        sysPlugin.setState(false).setUpdateTime(LocalDateTime.now());
        this.updateModel(sysPlugin);
        ExceptionUtil.throwEx(HexoExceptionEnum.ERROR_PLUGIN_CANNOT_DELETE);
    }

}
