package com.light.hexo.business.portal.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.light.hexo.business.admin.model.Category;
import com.light.hexo.business.admin.model.Tag;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @Author MoonlightL
 * @ClassName: Page
 * @ProjectName hexo-boot
 * @Description: 页面
 * @DateTime 2020/9/18 17:38
 */
@Setter
@Getter
@ToString
public class Page implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 页面标题
     */
    private String title;

    /**
     * 创建日期
     */
    @JsonProperty("date")
    private LocalDateTime createTime;

    /**
     * 修改日期
     */
    @JsonProperty("updated")
    private LocalDateTime updateTime;

    /**
     * 留言是否开启
     */
    @JsonProperty("comments")
    private Boolean isComment;

    /**
     * 布局名称
     */
    private String layout;

    /**
     * 文章内容
     */
    private String content;

    /**
     * 页面摘要
     */
    private String excerpt;

    /**
     * 页面网址
     */
    private String path;

    /**
     * 页面完整网址
     */
    private String permalink;

    /**
     * 上一页
     */
    private String prev;

    /**
     * 下一页
     */
    private String next;

    /**
     * 文章原始内容
     */
    private String raw;

    /**
     * 文章照片
     */
    @JsonProperty("photos")
    private List<String> photoList;

    /**
     * 文章外部链接
     */
    private String link;

    // ########################文章相关########################

    @JsonProperty("published")
    private Boolean isPublish;

    @JsonProperty("categories")
    private List<Category> categoryList;

    @JsonProperty("tags")
    private List<Tag> tagList;

    // ########################首页相关########################

    /**
     * 每页显示的文章数量
     */
    @JsonProperty("per_page")
    private Integer pageSize;

    /**
     * 总文章数
     */
    private Integer total;

    /**
     * 当前页
     */
    @JsonProperty("current")
    private Integer currentNum;

    /**
     * 当前页网址
     */
    @JsonProperty("current_url")
    private String currentUrl;

    /**
     * 上一页
     */
    @JsonProperty("prev")
    private Integer prevNum;

    /**
     * 上一页网址
     */
    @JsonProperty("prev_link")
    private String prevLink;

    /**
     * 下一页
     */
    @JsonProperty("next")
    private Integer nextNum;

    /**
     * 下一页网址
     */
    @JsonProperty("next_link")
    private String nextLink;


    // ########################归档相关########################

    /**
     * 是否是归档
     */
    @JsonProperty("archive")
    private Boolean isArchive;

    /**
     * 归档年份(4位)
     */
    private String year;

    /**
     * 归档月份(2位)
     */
    private String month;

    // ########################分类相关########################

    /**
     * 分类名称
     */
    @JsonProperty("category")
    private String categoryName;

    // ########################标签相关########################

    /**
     * 标签名称
     */
    @JsonProperty("tag")
    private String tagName;
}
