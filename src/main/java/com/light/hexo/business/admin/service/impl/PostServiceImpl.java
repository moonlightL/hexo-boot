package com.light.hexo.business.admin.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.light.hexo.business.admin.component.BaiDuPushService;
import com.light.hexo.business.admin.constant.ConfigEnum;
import com.light.hexo.business.admin.constant.HexoExceptionEnum;
import com.light.hexo.business.admin.mapper.PostMapper;
import com.light.hexo.business.admin.model.*;
import com.light.hexo.business.admin.model.event.PostEvent;
import com.light.hexo.business.admin.service.*;
import com.light.hexo.business.portal.constant.PageConstant;
import com.light.hexo.business.portal.model.HexoPageInfo;
import com.light.hexo.common.base.BaseMapper;
import com.light.hexo.common.base.BaseRequest;
import com.light.hexo.common.base.BaseServiceImpl;
import com.light.hexo.common.component.event.BaseEvent;
import com.light.hexo.common.component.event.EventEnum;
import com.light.hexo.common.component.event.EventPublisher;
import com.light.hexo.common.constant.CacheKey;
import com.light.hexo.common.constant.HexoConstant;
import com.light.hexo.common.exception.GlobalException;
import com.light.hexo.common.model.PostRequest;
import com.light.hexo.common.util.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.util.Sqls;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Author MoonlightL
 * @ClassName: PostServiceImpl
 * @ProjectName hexo-boot
 * @Description: 文章 Service 实现
 * @DateTime 2020/7/31 17:28
 */
@CacheConfig(cacheNames = "postCache")
@Service
@Slf4j
public class PostServiceImpl extends BaseServiceImpl<Post> implements PostService {

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private TagService tagService;

    @Autowired
    private PostTagService postTagService;

    @Autowired
    private ConfigService configService;

    @Autowired
    private BaiDuPushService baiDuPushService;

    @Autowired
    @Lazy
    private EventPublisher eventPublisher;

    private static final Integer COVER_NUM = 20;

    @Override
    public BaseMapper<Post> getBaseMapper() {
        return this.postMapper;
    }

    @Override
    protected Example getExample(BaseRequest request) {
        // 获取查询参数
        PostRequest postRequest = (PostRequest) request;
        Example example = Example.builder(Post.class)
                .select("id", "title", "link", "customLink", "customLink", "coverUrl", "categoryId",
                        "readNum", "commentNum", "praiseNum", "authCode",
                        "publishDate","publish", "comment", "top").orderByDesc("createTime").build();
        Example.Criteria criteria = example.createCriteria();

        Boolean publish = postRequest.getPublish();
        if (publish != null) {
            criteria.andEqualTo("publish", publish);
        }

        Boolean top = postRequest.getTop();
        if (top != null) {
            criteria.andEqualTo("top", top);
        }

        String title = postRequest.getTitle();
        if (StringUtils.isNotBlank(title)) {
            criteria.andLike("title", "%" + title.trim() + "%");
        }

        Integer categoryId = postRequest.getCategoryId();
        if (categoryId != null) {
            criteria.andEqualTo("categoryId", categoryId);
        }

        String publishDate = postRequest.getPublishDate();
        if (StringUtils.isNotBlank(publishDate)) {
            criteria.andEqualTo("publishDate", publishDate.trim());
        }

        String authCode = postRequest.getAuthCode();
        if (StringUtils.isNotBlank(authCode)) {
            if ("1".equals(authCode)) {
                criteria.andNotEqualTo("authCode", "");
            } else {
                criteria.andEqualTo("authCode", "");
            }
        }

        return example;
    }

    @Override
    public PageInfo<Post> findPage(BaseRequest<Post> request) throws GlobalException {

        PageInfo<Post> pageInfo = super.findPage(request);
        List<Post> list = pageInfo.getList();

        if (CollectionUtils.isEmpty(list)) {
            return new PageInfo<>();
        }

        List<Category> categoryList = this.categoryService.findAll();
        Map<Integer, Category> categoryMap = categoryList.stream().collect(Collectors.toMap(Category::getId, Function.identity(), (k1, k2)->k1));

        for (Post post : list) {
            Category category = categoryMap.get(post.getCategoryId());
            post.setCategoryName(category == null ? "默认" : category.getName());

            if (StringUtils.isNotBlank(post.getCustomLink())) {
                post.setLink(post.getCustomLink() + ".html");
            }
        }

        return pageInfo;
    }

    @Override
    public void removePostBatch(List<String> idStrList) throws GlobalException {
        List<Long> idList = idStrList.stream().map(Long::valueOf).collect(Collectors.toList());
        Example example = new Example(Post.class);
        example.createCriteria().andIn("id", idList);
        int num = this.getBaseMapper().deleteByExample(example);
        if (num > 0) {
            EhcacheUtil.clearByCacheName("postCache");
            EhcacheUtil.clearByCacheName("categoryCache");
            CacheUtil.remove(CacheKey.INDEX_COUNT_INFO);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void savePost(Post post) throws GlobalException {

        Category category = this.categoryService.findById(post.getCategoryId());
        if (category == null || !category.getState()) {
            ExceptionUtil.throwEx(HexoExceptionEnum.ERROR_CATEGORY_NOT_EXIST);
        }

        // 前端上传的参数可能带空格，影响条件查询
        post.setTitle(post.getTitle().trim());

        Example example = new Example(Post.class);
        example.createCriteria().andEqualTo("title", post.getTitle());
        Post dbPost = this.getBaseMapper().selectOneByExample(example);
        if (dbPost != null) {
            ExceptionUtil.throwEx(HexoExceptionEnum.ERROR_POST_TITLE_REPEAT);
        }

        if (StringUtils.isBlank(post.getAuthor())) {
            post.setAuthor(this.configService.getConfigValue(ConfigEnum.BLOG_AUTHOR.getName()));
        }

        String coverUrl = post.getCoverUrl();
        if(StringUtils.isBlank(coverUrl)) {
            int num = new Random().nextInt(COVER_NUM);
            coverUrl = HexoConstant.DEFAULT_IMG_DIR + "/post/post_cover_" + num + ".jpg";
            post.setCoverUrl(coverUrl);
        }

        boolean isMarkdown = this.configService.getConfigValue(ConfigEnum.EDITOR_TYPE.getName()).equals("markdown");
        String content = post.getContent();

        // 摘要
        post.setSummary(this.interceptContent(content, isMarkdown))
            .setSummaryHtml(this.interceptContentHtml(content, isMarkdown));

        if (isMarkdown) {
            post.setContentHtml(MarkdownUtil.md2html(post.getContent()));
        } else {
            post.setContentHtml(this.transformHtml(post.getContent()));
        }

        LocalDateTime now = LocalDateTime.now();
        if (post.getPublish() != null) {
            post.setPublishDate(now.toLocalDate().toString())
                .setYear(now.getYear() + "")
                .setMonth(DateUtil.fillTime(now.getMonth().getValue()))
                .setDay(DateUtil.fillTime(now.getDayOfMonth()))
                .setLink(post.getYear() + "/" + post.getMonth() + "/" + post.getDay() + "/" + StringUtils.replace(post.getTitle(), " ", "-") + "/");

            String customLink = post.getCustomLink();
            if (StringUtils.isNotBlank(customLink)) {
                if (customLink.startsWith("/")) {
                    post.setCustomLink(customLink.substring(1));
                }
            }

            this.baiDuPushService.push2BaiDu(post.getLink());
        }

        if (post.getTop() != null) {
            post.setTopTime(now);
            if (post.getTop()) {
                post.setCoverType(1);
            }
        }

        if (post.getComment() == null) {
            post.setComment(false);
        }

        if (post.getReprint() == null) {
            post.setReprint(false);
        }

        this.saveModel(post);

        this.saveTags(post, false);

        EhcacheUtil.clearByCacheName("postCache");
        EhcacheUtil.clearByCacheName("categoryCache");
        CacheUtil.remove(CacheKey.INDEX_COUNT_INFO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void editPost(Post post) throws GlobalException {

        Post dbPost = super.findById(post.getId());
        if (dbPost == null) {
            ExceptionUtil.throwEx(HexoExceptionEnum.ERROR_POST_NOT_EXIST);
        }

        Category category = this.categoryService.findById(post.getCategoryId());
        if (category == null) {
            ExceptionUtil.throwEx(HexoExceptionEnum.ERROR_CATEGORY_NOT_EXIST);
        }

        // 前端上传的参数可能带空格，影响条件查询
        dbPost.setTitle(post.getTitle().trim());

        Example example = new Example(Post.class);
        example.createCriteria().andEqualTo("title", post.getTitle());
        Post tmp = this.getBaseMapper().selectOneByExample(example);
        if (tmp != null && !tmp.getId().equals(dbPost.getId())) {
            ExceptionUtil.throwEx(HexoExceptionEnum.ERROR_POST_TITLE_REPEAT);
        }

        boolean isMarkdown = this.configService.getConfigValue(ConfigEnum.EDITOR_TYPE.getName()).equals("markdown");
        String content = post.getContent();

        // 摘要
        post.setSummary(this.interceptContent(content, isMarkdown))
            .setSummaryHtml(this.interceptContentHtml(content, isMarkdown));

        if (isMarkdown) {
            post.setContentHtml(MarkdownUtil.md2html(post.getContent()));
        } else {
            post.setContentHtml(this.transformHtml(post.getContent()));
        }

        LocalDateTime now = LocalDateTime.now();
        if (post.getPublish() != null) {
            String year,month,day;
            if (dbPost.getPublish()) {
                year = dbPost.getYear();
                month = dbPost.getMonth();
                day = dbPost.getDay();
            } else {
                post.setPublishDate(now.toLocalDate().toString());
                year = now.getYear() + "";
                month = DateUtil.fillTime(now.getMonth().getValue());
                day =  DateUtil.fillTime(now.getDayOfMonth());
            }

            String customLink = post.getCustomLink();
            if (StringUtils.isNotBlank(customLink)) {
                if (customLink.startsWith("/")) {
                    post.setCustomLink(customLink.substring(1));
                }
            }

            post.setLink(year + "/" + month + "/" + day + "/" + StringUtils.replace(post.getTitle(), " ", "-") + "/");

            this.baiDuPushService.push2BaiDu(post.getLink());
        }

        if (post.getTop() != null && !dbPost.getTop()) {
            post.setTopTime(now);
            if (post.getTop()) {
                post.setCoverType(1);
            }
        }

        if (post.getComment() == null) {
            post.setComment(false);
        }

        if (post.getReprint() == null) {
            post.setReprint(false);
        }

        this.updateModel(post);

        this.saveTags(post, true);

        EhcacheUtil.clearByCacheName("postCache");
        EhcacheUtil.clearByCacheName("categoryCache");
        CacheUtil.remove(CacheKey.INDEX_COUNT_INFO);
        CacheUtil.remove(PageConstant.MARKDOWN_KEY + ":" + post.getId() + ":1");
        CacheUtil.remove(PageConstant.MARKDOWN_KEY + ":" + post.getId() + ":2");
    }

    @Override
    public void importPostsByMd(String path) throws GlobalException {
        File dir = new File(path);
        if (!dir.isDirectory()) {
            ExceptionUtil.throwEx(HexoExceptionEnum.ERROR_IMPORT_PATH_NOT_DIR);
        }

        File[] files = dir.listFiles(pathname -> pathname.getName().endsWith("md"));
        if (files == null || files.length == 0) {
            ExceptionUtil.throwEx(HexoExceptionEnum.ERROR_IMPORT_PATH_HAVE_NO_MD);
        }

        List<Post> postList = this.packageToList(files);

        for (Post post : postList) {
            // 检测和插入
            this.postMapper.checkInsert(post);
            if (post.getId() != null) {
                this.saveTags(post, false);
            }
        }

        // 清理缓存
        EhcacheUtil.clearAll();
        CacheUtil.remove(CacheKey.INDEX_COUNT_INFO);
    }

    @Override
    public void importPostsByJson(String path) throws GlobalException {

        File file = new File(path);
        if (!file.exists()) {
            ExceptionUtil.throwEx(HexoExceptionEnum.ERROR_IMPORT_FILE_PATH_NOT_EXIST);
        }

        try {
            String jsonContent = FileUtils.readFileToString(file, "UTF-8");
            Map<String, Object> jsonMap = JsonUtil.string2Obj(jsonContent, Map.class);
            List<Map<String, Object>> recordList = (List<Map<String, Object>>) jsonMap.get("RECORDS");
            if (CollectionUtils.isEmpty(recordList)) {
                return;
            }

            List<Post> postList = this.packageToList(recordList);

            for (Post post : postList) {
                // 检测和插入
                this.postMapper.checkInsert(post);
                if (post.getId() != null) {
                    this.saveTags(post, false);
                }
            }

            // 清理缓存
            EhcacheUtil.clearAll();
            CacheUtil.remove(CacheKey.INDEX_COUNT_INFO);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void updateState(Post post) throws GlobalException {

        Post dbPost = super.findById(post.getId());
        if (dbPost == null) {
            ExceptionUtil.throwEx(HexoExceptionEnum.ERROR_POST_NOT_EXIST);
        }

        if (post.getTop() != null) {
            post.setTopTime(LocalDateTime.now());
            if (post.getTop()) {
                post.setCoverType(1);
            }
        }

        this.updateModel(post);
        EhcacheUtil.clearByCacheName("postCache");
        EhcacheUtil.clearByCacheName("categoryCache");
    }

    @Override
    public boolean isExistByCategoryId(Integer categoryId) throws GlobalException {

        Example example = Example.builder(Post.class)
                .select("id")
                .where(Sqls.custom()
                .andEqualTo("categoryId", categoryId))
                .build();

        return this.getBaseMapper().selectCountByExample(example) > 0;
    }

    @Override
    public int getPostNum() throws GlobalException {
        Example example = new Example(Post.class);
        example.createCriteria().andEqualTo("delete", false)
                                .andEqualTo("publish", true);
        return this.getBaseMapper().selectCountByExample(example);
    }

    @Override
    public List<Post> listTop5() throws GlobalException {
        Example example = Example.builder(Post.class)
                .select("id", "title", "link", "customLink", "readNum")
                .orderByDesc("readNum")
                .build();
        PageHelper.startPage(1, 5);
        List<Post> postList = this.getBaseMapper().selectByExample(example);
        if (CollectionUtils.isEmpty(postList)) {
            return postList;
        }

        for (Post post : postList) {
            if (StringUtils.isNotBlank(post.getCustomLink())) {
                post.setLink(post.getCustomLink() + ".html");
            }
        }

        return postList;
    }

    @Override
    public void publishPost(Integer id) throws GlobalException {
        Post dbPost = super.findById(id);
        if (dbPost == null) {
            ExceptionUtil.throwEx(HexoExceptionEnum.ERROR_POST_NOT_EXIST);
        }

        Post post = new Post();
        post.setId(id);
        post.setPublish(true);
        LocalDate now = LocalDate.now();
        post.setPublishDate(now.toString())
            .setYear(now.getYear() + "")
            .setMonth(DateUtil.fillTime(now.getMonth().getValue()))
            .setDay(DateUtil.fillTime(now.getDayOfMonth()))
            .setLink(post.getYear() + "/" + post.getMonth() + "/" + post.getDay() + "/" + StringUtils.replace(post.getTitle(), " ", "-") + "/");
        this.updateModel(post);
        this.baiDuPushService.push2BaiDu(post.getLink());
        EhcacheUtil.clearByCacheName("postCache");
        EhcacheUtil.clearByCacheName("categoryCache");
        CacheUtil.remove(CacheKey.INDEX_COUNT_INFO);
    }

    @Override
    public List<Post> listPostByIdList(List<Integer> postIdList) throws GlobalException {
        Example.Builder select = Example.builder(Post.class).select("id", "title", "link", "customLink", "coverUrl");
        if (!CollectionUtils.isEmpty(postIdList)) {
            select.where(Sqls.custom().andIn("id", postIdList));
        }
        select.orderByDesc("createTime");
        Example example = select.build();
        List<Post> postList = this.getBaseMapper().selectByExample(example);
        if (CollectionUtils.isEmpty(postList)) {
            return postList;
        }

        for (Post post : postList) {
            if (StringUtils.isNotBlank(post.getCustomLink())) {
                post.setLink(post.getCustomLink() + ".html");
            }
        }

        return postList;
    }

    @Cacheable(key = "'"+ PageConstant.POST_PAGE + ":' + #pageNum")
    @Override
    public HexoPageInfo pagePostsByIndex(int pageNum, int pageSize, boolean filterTop) throws GlobalException {
        Example.Builder builder = Example.builder(Post.class)
                .select("id", "title", "summary", "summaryHtml", "author", "publishDate", "year", "month", "day", "top", "reprint", "authCode",
                        "coverUrl", "coverType", "link", "customLink", "categoryId", "tags", "readNum", "praiseNum", "commentNum", "topTime", "createTime")
                .where(Sqls.custom().andEqualTo("publish", true).andEqualTo("delete", false));
        if (filterTop) {
            builder.andWhere(Sqls.custom().andEqualTo("top", false));
        }

        Example example = builder.orderByDesc("createTime").build();
        List<Post> postList = this.getBaseMapper().selectByExample(example);
        if (postList.isEmpty()) {
            return new HexoPageInfo(pageNum, pageSize, postList.size(), null);
        }

        List<Post> list = new ArrayList<>(pageSize);
        // 置顶文章
        if (!filterTop) {
            List<Post> topList = postList.stream().filter(Post::getTop)
                    .sorted(Comparator.comparing(Post::getTopTime).reversed())
                    .collect(Collectors.toList());
            list.addAll(topList);
        }

        // 非置顶文章
        List<Post> remainList = postList.stream().filter(i -> !i.getTop())
                .sorted(Comparator.comparing(Post::getPublishDate).reversed().thenComparing(
                        Comparator.comparing(Post::getCreateTime).reversed()))
                .collect(Collectors.toList());
        list.addAll(remainList);

        // 逻辑分页
        int start = (pageNum - 1) * pageSize;
        if (start > list.size()) {
            return new HexoPageInfo(pageNum, pageSize, list.size(), null);
        }

        List<Post> subList = this.paging(start, pageSize, list);
        // 查询分类
        List<Integer> categoryIdList = subList.stream().map(Post::getCategoryId).collect(Collectors.toList());
        List<Category> categoryList = this.categoryService.listCategory(categoryIdList);
        Map<Integer, Category> categoryMap = categoryList.stream().collect(Collectors.toMap(Category::getId, Function.identity(), (k1, k2)->k1));

        for (Post post : subList) {
            Category category = categoryMap.get(post.getCategoryId());
            if (category != null) {
                post.setCategoryName(category.getName());
            }

            if (StringUtils.isNotBlank(post.getCustomLink())) {
                post.setLink(post.getCustomLink() + ".html");
            }
        }

        return new HexoPageInfo(pageNum, pageSize, list.size(), subList);
    }

    @Cacheable(key = "'" + PageConstant.POST_ARCHIVE_ALL + "'")
    @Override
    public HexoPageInfo archivePostsByIndex() throws GlobalException {
        Example example = Example.builder(Post.class)
                .select("id", "title", "author", "publishDate", "year", "month", "day", "top", "reprint",
                        "coverUrl", "coverType", "link", "customLink", "categoryId", "tags", "readNum", "createTime")
                .where(Sqls.custom().andEqualTo("publish", true).andEqualTo("delete", false))
                .orderByDesc("createTime")
                .build();
        List<Post> postList = this.getBaseMapper().selectByExample(example);
        if (postList.isEmpty()) {
            return new HexoPageInfo(0, 10, postList.size(), null);
        }

        for (Post post : postList) {
            if (StringUtils.isNotBlank(post.getCustomLink())) {
                post.setLink(post.getCustomLink() + ".html");
            }
        }

        Map<String, List<Post>> map = postList.stream().collect(Collectors.groupingBy(Post::getYear));
        // key 降序排序
        Map<String, List<Post>> sortMap = new TreeMap<>(Comparator.reverseOrder());
        sortMap.putAll(map);
        return new HexoPageInfo(0, 10, postList.size(), sortMap);
    }

    @Cacheable(key = "'" + PageConstant.POST_ARCHIVE + ":' + #pageNum")
    @Override
    public HexoPageInfo archivePostsByIndex(Integer pageNum, Integer pageSize) throws GlobalException {
        Example example = Example.builder(Post.class)
                .select("id", "title", "author", "publishDate", "year", "month", "day", "top", "reprint",
                        "coverUrl", "coverType", "link", "customLink", "categoryId", "tags", "readNum", "praiseNum", "createTime")
                .where(Sqls.custom().andEqualTo("publish", true).andEqualTo("delete", false))
                .orderByDesc("createTime")
                .build();
        List<Post> postList = this.getBaseMapper().selectByExample(example);
        if (postList.isEmpty()) {
            return new HexoPageInfo(pageNum, pageSize, postList.size(), null);
        }

        for (Post post : postList) {
            if (StringUtils.isNotBlank(post.getCustomLink())) {
                post.setLink(post.getCustomLink() + ".html");
            }
        }

        // 逻辑分页
        int start = (pageNum - 1) * pageSize;

        if (start > postList.size()) {
            return new HexoPageInfo(pageNum, pageSize, postList.size(), null);
        }

        List<Post> subList = this.paging(start, pageSize, postList);
        Map<String, List<Post>> map = subList.stream().collect(Collectors.groupingBy(Post::getYear));
        // key 降序排序
        Map<String, List<Post>> sortMap = new TreeMap<>(Comparator.reverseOrder());
        sortMap.putAll(map);
        return new HexoPageInfo(pageNum, pageSize, postList.size(), sortMap);
    }

    private List<Post> paging(int start, int pageSize, List<Post> postList) {
        int end;
        if ((postList.size() - start) > pageSize) {
            end = start + pageSize;
        } else {
            int tmp = (postList.size() - start);
            if (tmp % pageSize == 0) {
                end = start + pageSize;
            } else {
                end = start + tmp;
            }
        }

        return postList.subList(start, end);
    }

    @Cacheable(key = "'" + PageConstant.POST_DETAIL_INFO + ":' + #link")
    @Override
    public Post getDetailInfo(String link, Integer linkType) throws GlobalException {

        Example.Builder builder = Example.builder(Post.class)
                .select("id", "title", "author", "contentHtml", "publishDate", "year", "month", "day", "top", "reprint", "reprintLink",
                        "coverUrl", "coverType", "link", "customLink", "categoryId", "tags", "readNum", "praiseNum", "commentNum", "authCode",
                        "topTime", "createTime");
        if (linkType.equals(1)) {
            builder.where(Sqls.custom().andEqualTo("link", link).andEqualTo("delete", false));
        } else {
            builder.where(Sqls.custom().andEqualTo("customLink", link).andEqualTo("delete", false));
        }

        Post post = this.getBaseMapper().selectOneByExample(builder.build());
        if (post == null) {
            ExceptionUtil.throwExToPage(HexoExceptionEnum.ERROR_POST_NOT_EXIST);
        }

        Category category = this.categoryService.findById(post.getCategoryId());
        if (category != null) {
            post.setCategoryName(StringUtils.isNotBlank(category.getName()) ? category.getName() : "默认");
        }

        Integer postId = post.getId();

        Post prevPost = this.postMapper.selectPreviousInfo(postId);
        post.setPrevPost(prevPost);

        Post nextPost = this.postMapper.selectNextInfo(postId);
        post.setNextPost(nextPost);

        return post;
    }

    @Cacheable(key = "'" + PageConstant.POST_DETAIL_PREVIOUS + ":' + #id")
    @Override
    public Post getPreviousInfo(Integer id) throws GlobalException {
        return this.postMapper.selectPreviousInfo(id);
    }

    @Cacheable(key = "'" + PageConstant.POST_DETAIL_NEXT + ":' + #id")
    @Override
    public Post getNextInfo(Integer id) throws GlobalException {
        return this.postMapper.selectNextInfo(id);
    }

    @Override
    public int praisePost(String ipAddr, Integer postId) throws GlobalException {
        String cacheKey = "prizePostCache" + ipAddr + ":" + postId;
        Object obj = CacheUtil.get(cacheKey);
        if (obj != null) {
            ExceptionUtil.throwEx(HexoExceptionEnum.ERROR_REPEAT_PRAISE_POST);
        }

        Post post = super.findById(postId);
        if (post == null) {
            ExceptionUtil.throwEx(HexoExceptionEnum.ERROR_POST_NOT_EXIST);
        }

        CacheUtil.put(cacheKey, postId, 60 * 1000);
        this.eventPublisher.emit(new PostEvent(postId, PostEvent.Type.PRAISE));
        return post.getPraiseNum() + 1;
    }

    @Override
    public Integer getPostNumByCategoryId(Integer categoryId) throws GlobalException {
        Example example = new Example(Post.class);
        example.createCriteria().andEqualTo("categoryId", categoryId)
                .andEqualTo("publish", true)
                .andEqualTo("delete", false);
        return this.getBaseMapper().selectCountByExample(example);
    }

    @Cacheable(key = "'" + PageConstant.POST_BY_CATEGORY_NAME + "' + #categoryName + ':' + #pageNum")
    @Override
    public List<Post> listPostsByCategoryName(String categoryName, Integer pageNum, Integer pageSize) throws GlobalException {

        Category category = this.categoryService.findByCategoryName(categoryName.trim());
        if (category == null || !category.getState()) {
            ExceptionUtil.throwExToPage(HexoExceptionEnum.ERROR_CATEGORY_NOT_EXIST);
        }

        PageHelper.startPage(pageNum, pageSize);
        Example example = Example.builder(Post.class)
                .select("id", "title", "summary", "author", "publishDate", "year", "month", "day", "top", "reprint",
                        "coverUrl", "coverType", "link", "customLink", "categoryId", "tags", "readNum", "praiseNum", "createTime")
                .where(Sqls.custom()
                        .andEqualTo("categoryId", category.getId())
                        .andEqualTo("publish", true)
                        .andEqualTo("delete", false))
                .orderByDesc("createTime")
                .build();
        List<Post> postList = this.getBaseMapper().selectByExample(example);
        if (CollectionUtils.isEmpty(postList)) {
            return postList;
        }

        for (Post post : postList) {
            if (StringUtils.isNotBlank(post.getCustomLink())) {
                post.setLink(post.getCustomLink() + ".html");
            }
        }

        return postList;
    }

    @Cacheable(key = "'" + PageConstant.POST_BY_TAG_NAME + "' + #tagName + ':' + #pageNum")
    @Override
    public List<Post> listPostsByTagName(String tagName, Integer pageNum, Integer pageSize) throws GlobalException {

        Tag tag = this.tagService.findByTagName(tagName.trim());
        if (tag == null) {
            ExceptionUtil.throwExToPage(HexoExceptionEnum.ERROR_TAG_NOT_EXIST);
        }

        PageHelper.startPage(pageNum, pageSize);
        return this.postMapper.selectListByTagId(tag.getId());
    }

    @Cacheable(key = "'" + PageConstant.POST_TOP_PAGE + "'")
    @Override
    public List<Post> findTopList() throws GlobalException {
        Example example = Example.builder(Post.class)
                .select("id", "title", "author", "publishDate", "year", "month", "day", "top", "reprint",
                        "coverUrl", "coverType", "link", "customLink", "categoryId", "tags", "readNum", "createTime")
                .where(Sqls.custom()
                        .andEqualTo("top", true)
                        .andEqualTo("delete", false))
                .orderByDesc("topTime")
                .build();
        List<Post> postList = this.getBaseMapper().selectByExample(example);
        if (CollectionUtils.isEmpty(postList)) {
            return postList;
        }

        for (Post post : postList) {
            if (StringUtils.isNotBlank(post.getCustomLink())) {
                post.setLink(post.getCustomLink() + ".html");
            }
        }

        // 查询分类
        List<Integer> categoryIdList = postList.stream().map(Post::getCategoryId).collect(Collectors.toList());
        List<Category> categoryList = this.categoryService.listCategory(categoryIdList);
        Map<Integer, Category> categoryMap = categoryList.stream().collect(Collectors.toMap(Category::getId, Function.identity(), (k1, k2)->k1));

        for (Post post : postList) {
            Category category = categoryMap.get(post.getCategoryId());
            if (category != null) {
                post.setCategoryName(category.getName());
            }
        }

        return postList;
    }

    @Override
    public List<Post> listEmptyHtml() throws GlobalException {
        Example example = Example.builder(Post.class)
                .select("id", "content")
                .where(Sqls.custom().andEqualTo("contentHtml", ""))
                .build();
        return this.getBaseMapper().selectByExample(example);
    }

    @Override
    public EventEnum getEventType() {
        return EventEnum.POST;
    }

    @Override
    public void dealWithEvent(BaseEvent event) {
        PostEvent postEvent = (PostEvent) event;
        Post post = this.findById(postEvent.getId());
        if (post == null) {
            return;
        }

        Post data = new Post();
        data.setId(post.getId());
        if (postEvent.getType().getCode().equals(PostEvent.Type.READ.getCode())) {
            data.setReadNum(post.getReadNum() + 1);
        } else if (postEvent.getType().getCode().equals(PostEvent.Type.PRAISE.getCode())) {
            data.setPraiseNum(post.getPraiseNum() + 1);
        } else if (postEvent.getType().getCode().equals(PostEvent.Type.COMMENT_ADD.getCode())) {
            data.setCommentNum(post.getCommentNum() + 1);
        } else if (postEvent.getType().getCode().equals(PostEvent.Type.COMMENT_MINUS.getCode())) {
            data.setCommentNum(post.getCommentNum() - 1);
        }
        data.setUpdateTime(LocalDateTime.now());
        this.updateModel(data);
    }

    private String interceptContent(String content, boolean isMarkdown) {
        StringBuilder sb = new StringBuilder();
        int index = content.indexOf("<!---->");
        if (index > -1) {
            String tmp = content.substring(0, index);
            if (StringUtils.isNotBlank(tmp)) {
                content = tmp;
            }
        }

        String html = isMarkdown ? MarkdownUtil.md2html(content) : content;

        Elements elements = Jsoup.parse(html).select("p");
        if (elements.size() > 3) {
            List<Element> elementList = elements.subList(0, 3);
            elementList.forEach(i -> {
                sb.append(i.text()).append("\n");
            });
        } else {
            elements.forEach(i -> {
                sb.append(i.text()).append("\n");
            });
        }

        String result = sb.toString();
        if (result.length() > 300) {
            result = result.substring(0, 300);
        }

        return result;
    }

    private String interceptContentHtml(String content, boolean isMarkdown) {
        StringBuilder sb = new StringBuilder();
        int index = content.indexOf("<!---->");
        if (index > -1) {
            String tmp = content.substring(0, index);
            if (StringUtils.isNotBlank(tmp)) {
                content = tmp;
            }
        }

        String html = isMarkdown ? MarkdownUtil.md2html(content) : content;
        Document document = Jsoup.parse(html);
        Elements elements = document.select("body");
        Element body = elements.get(0);
        Elements children = body.children();
        if (children.size() > 3) {
            List<Element> elementList = children.subList(0, 3);
            elementList.forEach(i -> {
                sb.append(i.toString()).append("\n");
            });
        } else {
            children.forEach(i -> {
                sb.append(i.toString()).append("\n");
            });
        }

        return sb.toString();
    }

    private String transformHtml(String html) {
        Document document = Jsoup.parse(html);

        // 转换 img
        Elements imgElements = document.select("img");
        for (Element img : imgElements) {
            String src = img.attr("src");
            img.removeAttr("src").attr("data-original", src).addClass("lazyload");
            img.wrap("<a class='fancybox' href='" + src + "' data-fancybox='gallery'></a>");
        }

        // 转换 table
        Elements tableElements = document.select("table");
        for (Element table : tableElements) {
            table.attr("class", "table");
            Elements trs = table.select("tr");
            int index = 0;
            StringBuilder sb = new StringBuilder();
            for (Element tr : trs) {
                Elements tds = tr.select("td");
                if (index == 0) {
                    // thead
                    sb.append("<thead><tr>");
                    for (Element td : tds) {
                        sb.append("<th>").append(td.text()).append("</th>");
                    }
                    sb.append("</tr></thead><tbody>");
                } else {
                    // 处理 tbody
                    sb.append("<tr>");
                    for (Element td : tds) {
                        sb.append("<td>").append(td.text()).append("</td>");
                    }
                    sb.append("</tr>");
                }
                index++;
            }
            sb.append("</tbody>");
            table.html(sb.toString());
        }

        // 转换 pre
        Elements preElements = document.select("pre");
        for (Element pre : preElements) {

            String data = pre.text().replaceAll("\r", "");
            String[] split = data.split("\n");
            int linNum = 1;

            Element figure = new Element("figure");
            figure.attr("class", "highlight");
            Element table = new Element("table");
            Element tbody = new Element("tbody");
            Element tr = new Element("tr");

            Element lineTd = new Element("td");
            lineTd.attr("class", "gutter");
            Element linePre = new Element("pre");
            for (String s : split) {
                Element span = new Element("span");
                span.attr("class", "line");
                span.text((linNum++) + "");
                linePre.appendChild(span).appendElement("br");
            }
            lineTd.appendChild(linePre);
            tr.appendChild(lineTd);

            Element contentTd = new Element("td");
            contentTd.attr("class", "code");
            Element contentPre = new Element("pre");
            for (String s : split) {
                Element span = new Element("span");
                span.attr("class", "line");
                span.text(s);
                contentPre.appendChild(span).appendElement("br");
            }
            contentTd.appendChild(contentPre);
            tr.appendChild(contentTd);

            tbody.appendChild(tr);
            table.appendChild(tbody);
            figure.appendChild(table);
            pre.replaceWith(figure);
        }

        return document.toString();
    }

    private void saveTags(Post post, boolean isEdit) {
        if (StringUtils.isBlank(post.getTags())) {
            return;
        }

        // 保存标签
        List<Integer> tagIdList = this.tagService.saveTagBatch(post.getTags().split(","));
        if (!CollectionUtils.isEmpty(tagIdList)) {

            if (isEdit) {
                // 修改操作，先删除文章关联的标签
                this.postTagService.deletePostTag(post.getId());
            }

            List<PostTag> list = new ArrayList<>(tagIdList.size());
            PostTag data;
            for (Integer tagId : tagIdList) {
                data = new PostTag(post.getId(), tagId);
                list.add(data);
            }

            this.postTagService.savePostTagBatch(list);
        }
    }

    private List<Post> packageToList(List<Map<String, Object>> recordList) throws UnsupportedEncodingException {

        List<Post> postList = new ArrayList<>(recordList.size());
        String author = this.configService.getConfigValue(ConfigEnum.BLOG_AUTHOR.getName());
        for (Map<String, Object> objectMap : recordList) {
            Post post = new Post();
            post.setTitle(objectMap.get("title").toString())
                    .setAuthor(author)
                    .setContent(objectMap.get("content").toString())
                    .setContentHtml(MarkdownUtil.md2html(post.getContent()))
                    .setSummary(this.interceptContent(post.getContent(), true))
                    .setSummaryHtml(this.interceptContentHtml(post.getContent(), true));

            // 设置封面
            int num = new Random().nextInt(COVER_NUM);
            post.setCoverUrl(HexoConstant.DEFAULT_IMG_DIR + "/post/post_cover_" + num + ".jpg");

            // 设置分类
            String categoryName = objectMap.get("category_name").toString();
            if (StringUtils.isBlank(categoryName)) {
                Category category = this.categoryService.getCategoryByName("默认");
                post.setCategoryId(category.getId());
            } else {
                post.setCategoryName(categoryName);
                Category category = this.categoryService.getCategoryByName(post.getCategoryName());
                if (category == null) {
                    category = new Category();
                    category.setName(post.getCategoryName())
                            .setState(true)
                            .setCoverUrl(HexoConstant.DEFAULT_CATEGORY_COVER)
                            .setSort(1)
                            .setRemark(post.getCategoryName());
                    this.categoryService.saveModel(category);
                }
                post.setCategoryId(category.getId());
            }

            String tags = objectMap.get("tags").toString();
            if (StringUtils.isNotBlank(tags)) {
                post.setTags(tags);
            }

            // 设置发表日期
            post.setPublish(true)
                .setPublishDate(objectMap.get("publish_date").toString())
                .setCreateTime(DateUtil.strToLocalDateTime(objectMap.get("create_time").toString()))
                .setUpdateTime(DateUtil.strToLocalDateTime(objectMap.get("update_time").toString()))
                .setYear(objectMap.get("year").toString())
                .setMonth(objectMap.get("month").toString())
                .setDay(objectMap.get("day").toString())
                .setLink(objectMap.get("post_url").toString())
                .setReadNum(Integer.parseInt(objectMap.get("read_count").toString()))
                .setPraiseNum(Integer.parseInt(objectMap.get("prize_count").toString()));

            post.setTop(false).setComment(true).setReprint(false);

            postList.add(post);
        }

        return postList;
    }

    private List<Post> packageToList(File[] files) {

        List<Post> postList = new ArrayList<>(files.length);
        BufferedReader br = null;
        Post post;
        String author = this.configService.getConfigValue(ConfigEnum.BLOG_AUTHOR.getName());
        for (File file : files) {
            try {
                br = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
                br.readLine();
                String titleStr = br.readLine();
                String createTimeStr = br.readLine();
                String tagsStr = br.readLine();
                String categoryNameStr = br.readLine();
                br.readLine();

                StringBuilder sb = new StringBuilder();
                String content;
                while ((content = br.readLine()) != null) {
                    sb.append(content).append("\r\n");
                }

                post = new Post();
                post.setTitle(titleStr.substring(titleStr.indexOf(":") + 1).trim())
                    .setAuthor(author)
                    .setContent(sb.toString())
                    .setContentHtml(MarkdownUtil.md2html(post.getContent()))
                    .setSummary(this.interceptContent(post.getContent(), true))
                    .setSummaryHtml(this.interceptContentHtml(post.getContent(), true));

                // 设置封面
                int num = new Random().nextInt(COVER_NUM);
                post.setCoverUrl(HexoConstant.DEFAULT_IMG_DIR + "/post/post_cover_" + num + ".jpg");

                // 设置分类
                String categoryName = categoryNameStr.substring(categoryNameStr.indexOf(":") + 1).trim();
                if (StringUtils.isBlank(categoryName)) {
                    Category category = this.categoryService.getCategoryByName("默认");
                    post.setCategoryId(category.getId());
                } else {
                    post.setCategoryName(categoryName);
                    Category category = this.categoryService.getCategoryByName(post.getCategoryName());
                    if (category == null) {
                        category = new Category();
                        category.setName(post.getCategoryName())
                                .setState(true)
                                .setCoverUrl(HexoConstant.DEFAULT_CATEGORY_COVER)
                                .setSort(1)
                                .setRemark(post.getCategoryName());
                        this.categoryService.saveModel(category);
                    }
                    post.setCategoryId(category.getId());
                }

                // 设置标签
                String tags = tagsStr.substring(tagsStr.indexOf(":") + 1).trim();
                if (StringUtils.isNotBlank(tags)) {
                    tags = tags.replace("[", "").replace("]", "");
                    post.setTags(tags);
                }

                // 设置发表日期
                LocalDateTime dateTime = DateUtil.strToLocalDateTime(createTimeStr.substring(createTimeStr.indexOf(":") + 1).trim());
                LocalDate localDate = dateTime.toLocalDate();
                post.setPublish(true)
                    .setPublishDate(localDate.toString())
                    .setCreateTime(dateTime)
                    .setUpdateTime(dateTime)
                    .setYear(localDate.getYear() + "")
                    .setMonth(DateUtil.fillTime(localDate.getMonth().getValue()))
                    .setDay(DateUtil.fillTime(localDate.getDayOfMonth()))
                    .setLink(post.getYear() + "/" + post.getMonth() + "/" + post.getDay() + "/" + post.getTitle().replace(" ", "-") + "/")
                    .setReadNum(0)
                    .setPraiseNum(0);

                post.setTop(false).setComment(true).setReprint(false);

                postList.add(post);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (br != null) {
                    try {
                        br.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return postList;
    }

}
