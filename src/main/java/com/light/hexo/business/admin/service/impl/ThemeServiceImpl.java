package com.light.hexo.business.admin.service.impl;

import cn.hutool.Hutool;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.StreamProgress;
import cn.hutool.core.lang.Console;
import cn.hutool.http.HttpUtil;
import com.light.hexo.business.admin.constant.HexoExceptionEnum;
import com.light.hexo.business.admin.mapper.ThemeMapper;
import com.light.hexo.business.admin.model.Theme;
import com.light.hexo.business.admin.model.ThemeExtend;
import com.light.hexo.business.admin.service.ConfigService;
import com.light.hexo.business.admin.service.ThemeExtendService;
import com.light.hexo.business.admin.service.ThemeService;
import com.light.hexo.common.base.BaseMapper;
import com.light.hexo.common.base.BaseRequest;
import com.light.hexo.common.base.BaseServiceImpl;
import com.light.hexo.common.constant.CacheKey;
import com.light.hexo.common.exception.GlobalException;
import com.light.hexo.common.model.ThemeRequest;
import com.light.hexo.common.model.TreeNode;
import com.light.hexo.common.util.CacheUtil;
import com.light.hexo.common.util.EhcacheUtil;
import com.light.hexo.common.util.ExceptionUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ResourceUtils;
import tk.mybatis.mapper.entity.Example;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author MoonlightL
 * @ClassName: ThemeServiceImpl
 * @ProjectName hexo-boot
 * @Description: 主题 Service 实现
 * @DateTime 2020/9/24 14:38
 */
@Service
public class ThemeServiceImpl extends BaseServiceImpl<Theme> implements ThemeService {

    @Autowired
    private ThemeMapper themeMapper;

    @Autowired
    private ThemeExtendService themeExtendService;

    @Override
    public BaseMapper<Theme> getBaseMapper() {
        return this.themeMapper;
    }

    @Override
    protected Example getExample(BaseRequest request) {
        ThemeRequest themeRequest = (ThemeRequest) request;
        Example example = new Example(Theme.class);
        Example.Criteria criteria = example.createCriteria();

        String name = themeRequest.getName();
        if (StringUtils.isNotBlank(name)) {
            criteria.andLike("name", name.trim() + "%");
        }

        example.orderBy("id").asc();

        return example;
    }


    @Override
    public Theme getActiveTheme(boolean cache) throws GlobalException {

        if (!cache) {
            Example example = new Example(Theme.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("state", 1);
            return this.themeMapper.selectOneByExample(example);
        }

        String key = CacheKey.CURRENT_THEME;
        Theme theme = CacheUtil.get(key);
        if (theme == null) {
            Example example = new Example(Theme.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("state", 1);
            theme = this.themeMapper.selectOneByExample(example);
            if (theme != null) {
                Map<String, String> configMap = this.themeExtendService.getThemeExtendMap(theme.getId());
                theme.setConfigMap(configMap);
                CacheUtil.put(key, theme);
            }
        }

        return theme;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void useTheme(Theme theme) throws GlobalException {
        Theme target = this.findById(theme.getId());
        if (target == null) {
            ExceptionUtil.throwEx(HexoExceptionEnum.ERROR_THEME_NOT_EXIST);
        }

        Theme currentTheme = this.getActiveTheme(false);
        if (currentTheme != null) {
            currentTheme.setState(false);
            this.updateModel(currentTheme);
        }

        target.setState(true);
        this.updateModel(target);

        CacheUtil.remove(CacheKey.CURRENT_THEME);
        EhcacheUtil.clearByCacheName("postCache");
    }

    @Override
    public Theme checkTheme(String name) throws GlobalException {
        Example example = new Example(Theme.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("name", name);
        return this.themeMapper.selectOneByExample(example);
    }

    @Override
    public void saveTheme(String name, String coverUrl, boolean state, String remark, List<Map<String, String>> extension) throws GlobalException {
        Theme theme = this.checkTheme(name);
        if (theme != null) {
            theme.setCoverUrl(coverUrl)
                 .setState(state)
                 .setRemark(remark);
           this.updateModel(theme);
        } else {
            theme = new Theme();
            theme.setName(name)
                 .setCoverUrl(coverUrl)
                 .setState(state)
                 .setSort(1)
                 .setRemark(remark);
            this.saveModel(theme);
        }

        this.themeExtendService.saveThemeExtend(theme.getId(), extension);

        Theme activeTheme = this.getActiveTheme(false);
        if (activeTheme == null) {
            this.useTheme(theme);
        }
    }

    @Override
    public void deleteThemeBatch(List<Theme> themeList) throws GlobalException {
        if (CollectionUtils.isEmpty(themeList)) {
            return;
        }

        // 删除主题
        List<Integer> idList = themeList.stream().map(Theme::getId).collect(Collectors.toList());
        super.removeBatch(idList);

        // 删除主题配置
        this.themeExtendService.deleteThemeExtendBatch(idList);

        Theme activeTheme = this.getActiveTheme(false);
        if (activeTheme == null || idList.contains(activeTheme.getId())) {
            List<Theme> allList = this.findAll();
            if (!CollectionUtils.isEmpty(allList)) {
                Theme theme = allList.get(0);
                this.useTheme(theme);
            }
        }
    }

    @Override
    public List<TreeNode> getThemeCatalog(Theme theme) throws GlobalException {
        try {
            File dir = ResourceUtils.getFile("classpath:templates/theme/" + theme.getName());
            return this.wrapTreeNode(dir, null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }



    @Override
    public String getThemeFileContent(String path) throws GlobalException, IOException {
        String extension = FilenameUtils.getExtension(path);
        if (extension.equals("png") || extension.equals("jpg")) {
            return "不能编辑图片";
        }

        File file = new File(path);
        if (!file.exists()) {
            return "文件不存在";
        }

        if (file.isDirectory()) {
            return "不能编辑文件目录";
        }

        return FileUtils.readFileToString(file, "UTF-8");
    }

    @Override
    public void editThemeFileContent(String path, String content) throws GlobalException, IOException {

        File file = new File(path);
        if (!file.exists()) {
            return;
        }

        FileUtils.writeStringToFile(file, content, "UTF-8", false);
    }

    @Override
    public void fetchTheme(String themeUrl) throws GlobalException {

        int index = themeUrl.indexOf("hexo-boot-theme");
        if (index < 0) {
            ExceptionUtil.throwEx(HexoExceptionEnum.ERROR_THEME_URL_WRONG);
        }

        String ext = FileUtil.extName(themeUrl);
        if (!"git".equals(ext)) {
            ExceptionUtil.throwEx(HexoExceptionEnum.ERROR_THEME_URL_WRONG);
        }

        String[] split = themeUrl.split("-");
        String tmp = split[split.length - 1];
        String themeName = tmp.split("\\.")[0];

        String path = this.getClass().getClassLoader().getResource("").getPath();
        File file = new File(path, "templates/theme");

        File dir = new File(file.getAbsolutePath(), themeName);
        if (dir.exists() && dir.isDirectory()) {
            FileUtil.del(dir);
        }

        InputStream in = null;
        Process process = null;
        String result = null;
        try {
            process = Runtime.getRuntime().exec("git clone " + themeUrl + " " + file.getAbsolutePath() + "/" +themeName);
            in = process.getInputStream();
            result = IOUtils.toString(in, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (process != null) {
                process.destroy();
            }

            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        System.out.println(result);
    }

    private List<TreeNode> wrapTreeNode(File dir, TreeNode parent) {
        List<TreeNode> treeNodeList = new ArrayList<>();
        File[] files = dir.listFiles();
        int index = 1;
        for (File file : files) {
            TreeNode treeNode = new TreeNode();
            treeNode.setId(index++);
            treeNode.setName(file.getName());
            treeNode.setPath(file.getAbsolutePath());
            if (parent != null) {
                treeNode.setPId(parent.getId());
                List<TreeNode> children = parent.getChildren();
                if (children == null) {
                    children = new ArrayList<>();
                    parent.setChildren(children);
                }
                children.add(treeNode);
            } else {
                treeNode.setPId(0);
            }

            if (file.isDirectory()) {
                treeNode.setParent(true);
                this.wrapTreeNode(file, treeNode);
            } else {
                treeNode.setParent(false);
            }

            treeNodeList.add(treeNode);
        }

        return treeNodeList;
    }

}
