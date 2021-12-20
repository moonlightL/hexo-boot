package com.light.hexo.business.admin.service.impl;

import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.util.ZipUtil;
import com.light.hexo.business.admin.config.BlogProperty;
import com.light.hexo.business.admin.constant.HexoExceptionEnum;
import com.light.hexo.business.admin.mapper.ThemeMapper;
import com.light.hexo.business.admin.model.Theme;
import com.light.hexo.business.admin.model.extend.ThemeFileExtension;
import com.light.hexo.business.admin.model.extend.ThemeFile;
import com.light.hexo.business.admin.service.ThemeExtendService;
import com.light.hexo.business.admin.service.ThemeService;
import com.light.hexo.common.base.BaseMapper;
import com.light.hexo.common.base.BaseRequest;
import com.light.hexo.common.base.BaseServiceImpl;
import com.light.hexo.common.constant.CacheKey;
import com.light.hexo.common.exception.GlobalException;
import com.light.hexo.common.model.ThemeRequest;
import com.light.hexo.common.model.TreeNode;
import com.light.hexo.common.util.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.util.FileUtil;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ResourceUtils;
import tk.mybatis.mapper.entity.Example;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @Author MoonlightL
 * @ClassName: ThemeServiceImpl
 * @ProjectName hexo-boot
 * @Description: 主题 Service 实现
 * @DateTime 2020/9/24 14:38
 */
@Service
@Slf4j
public class ThemeServiceImpl extends BaseServiceImpl<Theme> implements ThemeService {

    @Autowired
    private ThemeMapper themeMapper;

    @Autowired
    private ThemeExtendService themeExtendService;

    @Autowired
    private BlogProperty blogProperty;

    private static final String THEME_DIR = "templates/theme";

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

        example.orderBy("state").desc().orderBy("id").asc();

        return example;
    }


    @Override
    public Theme getActiveTheme(boolean cache) throws GlobalException {

        if (!cache) {
            Example example = new Example(Theme.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("state", 1);
            return this.getBaseMapper().selectOneByExample(example);
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
    public void useTheme(Integer themeId) throws GlobalException {
        Theme target = this.findById(themeId);
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
    public Theme getTheme(String name) throws GlobalException {
        Example example = new Example(Theme.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("name", name);
        return this.themeMapper.selectOneByExample(example);
    }

    @Override
    public Integer saveTheme(String name, String coverUrl, boolean state, String remark, List<ThemeFileExtension> extensionList) throws GlobalException {
        Theme theme = this.getTheme(name);
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

        this.themeExtendService.saveThemeExtend(theme.getId(), extensionList);

        return theme.getId();
    }

    @Override
    public void deleteThemeBatch(List<Integer> idList) throws GlobalException {
        if (CollectionUtils.isEmpty(idList)) {
            return;
        }

        // 删除主题
        super.removeBatch(idList);

        // 删除主题配置
        this.themeExtendService.deleteThemeExtendBatch(idList);

        Theme activeTheme = this.getActiveTheme(false);
        if (activeTheme == null || idList.contains(activeTheme.getId())) {
            List<Theme> allList = this.findAll();
            if (!CollectionUtils.isEmpty(allList)) {
                Theme theme = allList.get(0);
                this.useTheme(theme.getId());
            }
        }
    }

    @Override
    public List<TreeNode> getThemeTreeNode(Theme theme) throws GlobalException {
        try {
            File dir;
            URI themeUri = ResourceUtils.getURL(ResourceUtils.CLASSPATH_URL_PREFIX + THEME_DIR).toURI();

            if ("jar".equalsIgnoreCase(themeUri.getScheme())) {
                dir = ResourceUtils.getFile(ResourceUtils.FILE_URL_PREFIX + this.blogProperty.getThemeDir() + theme.getName());
            } else {
                dir = ResourceUtils.getFile(themeUri.toString() + File.separator + theme.getName());
            }

            return this.wrapTreeNode(dir, null);
        } catch (FileNotFoundException | URISyntaxException e) {
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
    public boolean fetchTheme(String themeUrl) throws GlobalException {

        int index = themeUrl.indexOf("hexo-boot-theme");
        if (index < 0) {
            ExceptionUtil.throwEx(HexoExceptionEnum.ERROR_THEME_URL_WRONG);
        }

        int extIndex = themeUrl.lastIndexOf(".git");
        if (extIndex < 0) {
            ExceptionUtil.throwEx(HexoExceptionEnum.ERROR_THEME_URL_WRONG);
        }

        String[] split = themeUrl.split("-");
        String tmp = split[split.length - 1];
        String themeName = tmp.split("\\.")[0];

        File file = this.getThemeCatalog(false);
        File dir = new File(file.getAbsolutePath(), themeName);
        if (dir.exists() && dir.isDirectory()) {
            FileUtils.deleteQuietly(dir);
        }

        try {
            // 控制 FileListener 的操作频率
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try(
            Git git = Git.cloneRepository()
                         .setURI(themeUrl)
                         .setDirectory(new File(file.getAbsolutePath(), themeName))
                         .call()
            ) {

            log.info("Cloning from " + themeUrl + " to " + git.getRepository());

            return true;
        } catch (GitAPIException e) {
            e.printStackTrace();
            log.info(e.getMessage());
        }

        return false;
    }

    @Override
    public String unzipTheme(InputStream inputStream) throws GlobalException {

        String tmpPath = System.getProperty("java.io.tmpdir");
        File tmpDir = new File(tmpPath, "themeDir");
        if (tmpDir.exists()) {
            FileUtils.deleteQuietly(tmpDir);
        }

        File unzipThemeCog = ZipUtil.unzip(inputStream, tmpDir, Charset.defaultCharset());

        // 获取刚解压的主题文件夹
        File[] childrenFile = unzipThemeCog.listFiles();
        if (childrenFile == null || childrenFile.length == 0) {
            ExceptionUtil.throwEx(HexoExceptionEnum.ERROR_THEME_UNZIP_WRONG);
        }

        File newThemeDir = childrenFile[0];

        // 读取 theme.json 文件
        File[] jsonFiles = newThemeDir.listFiles((dir, name) -> name.equals("theme.json"));
        if (jsonFiles == null || jsonFiles.length == 0) {
            ExceptionUtil.throwEx(HexoExceptionEnum.ERROR_THEME_COG_WRONG);
        }

        File jsonFile = jsonFiles[0];
        String themeName = "";
        try {
            // 读取内容
            String content = FileUtils.readFileToString(jsonFile, "UTF-8");
            ThemeFile themeFile = JsonUtil.string2Obj(content, ThemeFile.class);
            File file = this.getThemeCatalog(false);
            themeName = themeFile.getName();
            File themeDir = new File(file.getAbsolutePath(), themeName);
            if (themeDir.exists() && themeDir.isDirectory()) {
                FileUtils.deleteQuietly(themeDir);
            }
            FileUtil.copyDir(newThemeDir, themeDir);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return themeName;
    }

    @Override
    public File getThemeCatalog(boolean sync) throws GlobalException {
        File dir = null;
        try {
            URI uri = ResourceUtils.getURL(ResourceUtils.CLASSPATH_URL_PREFIX + THEME_DIR).toURI();

            if ("jar".equalsIgnoreCase(uri.getScheme())) {
                dir = ResourceUtils.getFile(ResourceUtils.FILE_URL_PREFIX + this.blogProperty.getThemeDir());
                if (!dir.exists() || dir.listFiles() == null || dir.listFiles().length == 0) {
                    FileSystem fileSystem = FileSystems.newFileSystem(uri, Collections.emptyMap());
                    Path source = fileSystem.getPath("/BOOT-INF/classes/" + THEME_DIR);
                    Path target = dir.toPath();

                    Files.walkFileTree(source, new SimpleFileVisitor<Path>() {

                        @Override
                        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                            Path current = target.resolve(source.relativize(dir).toString());
                            Files.createDirectories(current);
                            return FileVisitResult.CONTINUE;
                        }

                        @Override
                        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                            Files.copy(file, target.resolve(source.relativize(file).toString()), StandardCopyOption.REPLACE_EXISTING);
                            return FileVisitResult.CONTINUE;
                        }
                    });
                }
            } else {
                dir = ResourceUtils.getFile(uri);
            }
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        }

        return dir;
    }

    @Override
    public void checkThemeByStartup() throws GlobalException, IOException {
        // 获取主题目录
        File dir = this.getThemeCatalog(true);
        if (dir == null) {
            return;
        }

        Theme activeTheme = this.getActiveTheme(false);

        // 实际存在的主题名称
        List<String> themeNameList = new ArrayList<>();
        for (File file : Objects.requireNonNull(dir.listFiles())) {

            File[] jsonFiles = file.listFiles((i, name) -> name.equals("theme.json"));
            if (jsonFiles == null) {
                continue;
            }

            File jsonFile = jsonFiles[0];

            // 读取内容
            String content = FileUtils.readFileToString(jsonFile, "UTF-8");
            ThemeFile themeFile = JsonUtil.string2Obj(content, ThemeFile.class);

            String themeName = themeFile.getName();
            if (StringUtils.isBlank(themeName)) {
                continue;
            }

            themeNameList.add(themeName);

            boolean state = false;
            if (activeTheme == null) {
                if (themeName.equals("default")) {
                    state = true;
                }
            } else {
                if (activeTheme.getName().equals(themeName)) {
                    state = true;
                }
            }

            String fileDir = jsonFile.getParentFile().getName();
            Integer themeId = this.saveTheme(
                    themeName,
                    String.format("/theme/%s/preview.png", fileDir),
                    state,
                    StringUtils.isNotBlank(themeFile.getRemark()) ? themeFile.getRemark(): "",
                    themeFile.getExtension()
            );

            if (state) {
                this.useTheme(themeId);
            }
        }

        // 过滤出不在 theme 目录的主题记录
        List<Theme> themeList = this.findAll();
        List<Theme> filterList = themeList.stream().filter(i -> !themeNameList.contains(i.getName())).collect(Collectors.toList());
        List<Integer> idList = filterList.stream().map(Theme::getId).collect(Collectors.toList());
        this.deleteThemeBatch(idList);
    }

    @Override
    public void removeTheme(Integer id) throws GlobalException {

        Theme theme = super.findById(id);
        if (theme == null) {
            ExceptionUtil.throwEx(HexoExceptionEnum.ERROR_THEME_NOT_EXIST);
        }

        Theme activeTheme = this.getActiveTheme(false);
        if (activeTheme != null && activeTheme.getId().equals(id)) {
            ExceptionUtil.throwEx(HexoExceptionEnum.ERROR_ACTIVE_THEME_CANNOT_REMOVE);
        }

        File file = this.getThemeCatalog(false);
        File dir = new File(file.getAbsolutePath(), theme.getName());
        if (dir.exists() && dir.isDirectory()) {
            FileUtils.deleteQuietly(dir);
        }

    }

    private List<TreeNode> wrapTreeNode(File dir, TreeNode parent) {
        List<TreeNode> treeNodeList = new ArrayList<>();
        File[] files = dir.listFiles();
        if (files == null) {
            return new ArrayList<>();
        }

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
