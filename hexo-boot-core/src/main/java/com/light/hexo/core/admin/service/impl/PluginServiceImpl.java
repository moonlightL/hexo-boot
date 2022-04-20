package com.light.hexo.core.admin.service.impl;

import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.util.ZipUtil;
import com.light.hexo.common.base.BaseRequest;
import com.light.hexo.common.base.BaseServiceImpl;
import com.light.hexo.common.exception.GlobalException;
import com.light.hexo.common.request.PluginRequest;
import com.light.hexo.common.util.ExceptionUtil;
import com.light.hexo.core.admin.config.BlogConfig;
import com.light.hexo.core.admin.constant.HexoExceptionEnum;
import com.light.hexo.core.admin.service.PluginService;
import com.light.hexo.mapper.base.BaseMapper;
import com.light.hexo.mapper.mapper.PluginMapper;
import com.light.hexo.mapper.model.Plugin;
import org.apache.commons.lang3.StringUtils;
import org.pf4j.PluginDescriptor;
import org.pf4j.PluginManager;
import org.pf4j.PluginWrapper;
import org.pf4j.spring.SpringPluginManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;
import java.io.File;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.time.LocalDateTime;

/**
 * @Author MoonlightL
 * @ClassName: PluginServiceImpl
 * @ProjectName hexo-boot
 * @Description: 插件 Service 实现
 * @DateTime 2022/4/13, 0013 10:34
 */
@Service
public class PluginServiceImpl extends BaseServiceImpl<Plugin> implements PluginService {

    @Autowired
    private PluginMapper pluginMapper;

    @Autowired
    private BlogConfig blogConfig;

    @Autowired
    private PluginManager pluginManager;

    @Override
    public BaseMapper<Plugin> getBaseMapper() {
        return this.pluginMapper;
    }

    @Override
    protected Example getExample(BaseRequest request) {
        PluginRequest pluginRequest = (PluginRequest) request;
        Example example = new Example(Plugin.class);

        Example.Criteria criteria = example.createCriteria();

        String name = pluginRequest.getName();
        if (StringUtils.isNotBlank(name)) {
            criteria.andLike("name", name.trim() + "%");
        }

        return example;
    }

    @Override
    public void unzipPlugin(String originalFilename, InputStream inputStream) throws GlobalException {

        String pluginDir = this.blogConfig.getPluginDir();
        File parentDir = new File(pluginDir);
        if (!parentDir.exists()) {
            parentDir.mkdirs();
        }

        File unzipPluginCog = ZipUtil.unzip(inputStream, parentDir, Charset.defaultCharset());
        File[] childrenFile = unzipPluginCog.listFiles(pathname -> pathname.getName().endsWith("jar"));
        if (childrenFile == null || childrenFile.length == 0) {
            return;
        }

        File jarFile = childrenFile[0];
        String filePath = jarFile.getAbsolutePath();

        String pluginId = this.pluginManager.loadPlugin(Paths.get(filePath));

        Example example = new Example(Plugin.class);
        example.createCriteria().andEqualTo("name", pluginId);
        Plugin dbPlugin = this.pluginMapper.selectOneByExample(example);
        if (dbPlugin != null) {
            ExceptionUtil.throwEx(HexoExceptionEnum.ERROR_PLUGIN_HAD_INSTALLED);
        }

        PluginWrapper pluginWrapper = this.pluginManager.getPlugin(pluginId);
        PluginDescriptor descriptor = pluginWrapper.getDescriptor();

        Plugin plugin = new Plugin();
        plugin.setName(pluginId)
              .setState(false)
              .setRemark(descriptor.getPluginDescription())
              .setVersion(descriptor.getVersion())
              .setAuthor(descriptor.getProvider())
              .setFilePath(filePath)
              .setCreateTime(LocalDateTime.now())
              .setUpdateTime(plugin.getCreateTime());
        this.pluginMapper.insert(plugin);
    }

    @Override
    public void updatePlugin(Plugin plugin) throws GlobalException {

        Plugin dbPlugin = super.findById(plugin.getId());
        if (dbPlugin == null) {
            ExceptionUtil.throwEx(HexoExceptionEnum.ERROR_PLUGIN_NOT_EXIST);
        }

        Boolean state = plugin.getState();
        if (state) {
            this.pluginManager.startPlugin(dbPlugin.getName());
        } else {
            this.pluginManager.stopPlugin(dbPlugin.getName());
        }

        super.updateModel(plugin);
    }

    @Override
    public void removePlugin(Integer id) throws GlobalException {

        Plugin dbPlugin = super.findById(id);
        if (dbPlugin == null) {
            ExceptionUtil.throwEx(HexoExceptionEnum.ERROR_PLUGIN_NOT_EXIST);
        }

        String pluginId = dbPlugin.getName();
        if (dbPlugin.getState()) {
            this.pluginManager.stopPlugin(pluginId);
        }

        this.pluginManager.unloadPlugin(pluginId);

        super.removeModel(id);
    }

}
