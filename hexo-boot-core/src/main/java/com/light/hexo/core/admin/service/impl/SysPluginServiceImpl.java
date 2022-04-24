package com.light.hexo.core.admin.service.impl;

import cn.hutool.core.util.ZipUtil;
import cn.hutool.system.oshi.OshiUtil;
import com.light.hexo.common.base.BaseRequest;
import com.light.hexo.common.base.BaseServiceImpl;
import com.light.hexo.common.exception.GlobalException;
import com.light.hexo.common.plugin.BasePlugin;
import com.light.hexo.common.plugin.HexoBootPluginManager;
import com.light.hexo.common.request.PluginRequest;
import com.light.hexo.common.util.DateUtil;
import com.light.hexo.common.util.ExceptionUtil;
import com.light.hexo.core.admin.config.BlogConfig;
import com.light.hexo.core.admin.constant.HexoExceptionEnum;
import com.light.hexo.core.admin.service.SysPluginService;
import com.light.hexo.mapper.base.BaseMapper;
import com.light.hexo.mapper.mapper.SysPluginMapper;
import com.light.hexo.mapper.model.SysPlugin;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.pf4j.*;
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
public class SysPluginServiceImpl extends BaseServiceImpl<SysPlugin> implements SysPluginService {

    @Autowired
    private SysPluginMapper pluginMapper;

    @Autowired
    private BlogConfig blogConfig;

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

        String name = pluginRequest.getName();
        if (StringUtils.isNotBlank(name)) {
            criteria.andLike("name", name.trim() + "%");
        }

        return example;
    }

    @Override
    public int removeModel(Serializable id) throws GlobalException {
        return super.removeModel(id);
    }

    @Override
    public void installPlugin(String originalFilename, InputStream inputStream) throws GlobalException {

        Path pluginsRoot = this.pluginManager.getPluginsRoot();
        String parentDirPath = pluginsRoot.toString();
        File unzipPluginCog = ZipUtil.unzip(inputStream, new File(parentDirPath), Charset.defaultCharset());
        File jarFile = new File(unzipPluginCog.getAbsolutePath(), FilenameUtils.getBaseName(originalFilename) + ".jar");
        if (!jarFile.exists()) {
            ExceptionUtil.throwEx(HexoExceptionEnum.ERROR_PLUGIN_INVALID);
        }

        String filePath = jarFile.getAbsolutePath();

        String pluginId = null;
        try {
            pluginId = this.pluginManager.loadPlugin(Paths.get(filePath));
        } catch (Exception e) {
            if (e instanceof PluginAlreadyLoadedException) {
                // do nothing
            } else if (e instanceof PluginRuntimeException) {
                ExceptionUtil.throwEx(HexoExceptionEnum.ERROR_PLUGIN_INVALID);
            }
        }

        PluginWrapper pluginWrapper = this.pluginManager.getPlugin(pluginId);
        PluginDescriptor descriptor = pluginWrapper.getDescriptor();

        Example example = new Example(SysPlugin.class);
        example.createCriteria().andEqualTo("name", pluginId);
        SysPlugin dbPlugin = this.pluginMapper.selectOneByExample(example);
        if (dbPlugin != null) {
            ExceptionUtil.throwEx(HexoExceptionEnum.ERROR_PLUGIN_INSTALLED);
        }

        this.pluginManager.startPlugin(pluginId, filePath);

        BasePlugin basePlugin = (BasePlugin) pluginWrapper.getPlugin();

        SysPlugin plugin = new SysPlugin();
        plugin.setName(pluginId)
              .setState(true)
              .setRemark(descriptor.getPluginDescription())
              .setVersion(descriptor.getVersion())
              .setAuthor(descriptor.getProvider())
              .setFilePath(filePath)
              .setConfigUrl(basePlugin.getConfigUrl())
              .setCreateTime(LocalDateTime.now())
              .setUpdateTime(plugin.getCreateTime());
        this.pluginMapper.insert(plugin);
    }

    @Override
    public void updatePlugin(SysPlugin plugin) throws GlobalException {

        SysPlugin dbPlugin = super.findById(plugin.getId());
        if (dbPlugin == null) {
            ExceptionUtil.throwEx(HexoExceptionEnum.ERROR_PLUGIN_NOT_EXIST);
        }

        try {
            String pluginId = dbPlugin.getName();
            PluginState pluginState = plugin.getState() ? this.pluginManager.startPlugin(pluginId, dbPlugin.getFilePath()) : this.pluginManager.stopPlugin(pluginId);
            if (pluginState.toString().equals(PluginState.STARTED.toString())
            || pluginState.toString().equals(PluginState.STOPPED.toString())) {
                super.updateModel(plugin);
            }

        } catch (Exception e) {
            ExceptionUtil.throwEx(HexoExceptionEnum.ERROR_PLUGIN_MODIFY_STATE);
        }
    }

    @Override
    public void uninstallPlugin(Integer id) throws GlobalException {

        SysPlugin dbPlugin = super.findById(id);
        if (dbPlugin == null) {
            ExceptionUtil.throwEx(HexoExceptionEnum.ERROR_PLUGIN_NOT_EXIST);
        }

        String pluginId = dbPlugin.getName();

        this.pluginManager.stopPlugin(pluginId);

        boolean unloadResult = this.pluginManager.unloadPlugin(pluginId);
        if (!unloadResult) {
            ExceptionUtil.throwEx(HexoExceptionEnum.ERROR_PLUGIN_CANNOT_UNLOAD);
        }

        this.deletePlugin(dbPlugin);
    }

    private void deletePlugin(SysPlugin sysPlugin) {
        File pluginFile = new File(sysPlugin.getFilePath());
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

                boolean deleteQuietly = FileUtils.deleteQuietly(pluginFile);
                if (!deleteQuietly) {
                    sysPlugin.setState(false).setUpdateTime(LocalDateTime.now());
                    super.updateModel(sysPlugin);
                    ExceptionUtil.throwEx(HexoExceptionEnum.ERROR_PLUGIN_CANNOT_DELETE);
                }

                super.removeModel(sysPlugin.getId());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
