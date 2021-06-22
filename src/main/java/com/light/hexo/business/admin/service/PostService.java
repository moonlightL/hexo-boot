package com.light.hexo.business.admin.service;

import com.light.hexo.business.admin.model.Post;
import com.light.hexo.business.portal.model.HexoPageInfo;
import com.light.hexo.common.base.BaseService;
import com.light.hexo.common.component.event.EventService;
import com.light.hexo.common.exception.GlobalException;

import java.util.List;
import java.util.Map;

/**
 * @Author MoonlightL
 * @ClassName: PostService
 * @ProjectName hexo-boot
 * @Description: 文章 Service
 * @DateTime 2020/7/31 17:28
 */
public interface PostService extends BaseService<Post>, EventService {

    /**
     * 批量删除
     * @param idStrList
     * @throws GlobalException
     */
    void removePostBatch(List<String> idStrList) throws GlobalException;

    /**
     * 保存
     * @param post
     * @throws GlobalException
     */
    void savePost(Post post) throws GlobalException;

    /**
     * 修改
     * @param post
     * @throws GlobalException
     */
    void editPost(Post post) throws GlobalException;

    /**
     * 通过 md 文件导入导入文章
     * @param path
     * @throws GlobalException
     */
    void importPostsByMd(String path) throws GlobalException;

    /**
     * 通过 json 文件导入文章
     * @param path
     * @throws GlobalException
     */
    void importPostsByJson(String path) throws GlobalException;

    /**
     * 修改状态（评论、置顶）
     * @param post
     * @throws GlobalException
     */
    void updateState(Post post) throws GlobalException;

    /**
     * 检测分类下是否存在文章
     * @param categoryId
     * @return
     * @throws GlobalException
     */
    boolean isExistByCategoryId(Integer categoryId) throws GlobalException;

    /**
     * 查询文章数
     * @return
     * @throws GlobalException
     */
    int getPostNum() throws GlobalException;

    /**
     * 获取 top5 文章列表
     * @return
     * @throws GlobalException
     */
    List<Post> listTop5() throws GlobalException;

    /**
     * 发表文章
     * @param id
     * @throws GlobalException
     */
    void publishPost(Integer id) throws GlobalException;

    /**
     * 查询文章列表
     * @param postIdList
     * @return
     * @throws GlobalException
     */
    List<Post> listPostByIdList(List<Integer> postIdList) throws GlobalException;


    // ================================= 以下为前端页面请求 ===============================

    /**
     * 首页文章列表
     * @param pageNum
     * @param pageSize
     * @param filterTop 是否过滤掉指定文章
     * @return
     */
    HexoPageInfo pagePostsByIndex(int pageNum, int pageSize, boolean filterTop) throws GlobalException;

    /**
     * 获取所有归档文章列表
     * @return
     * @throws GlobalException
     */
    HexoPageInfo archivePostsByIndex() throws GlobalException;

    /**
     * 归档文章列表
     * @param pageNum
     * @param pageSize
     * @return
     * @throws GlobalException
     */
    HexoPageInfo archivePostsByIndex(Integer pageNum, Integer pageSize) throws GlobalException;

    /**
     * 获取文章详情
     * @param link
     * @param linkType 链接方式 1:原始 2：自定义
     * @return
     * @throws GlobalException
     */
    Post getDetailInfo(String link, Integer linkType) throws GlobalException;

    /**
     * 获取上一页文章信息
     * @param id
     * @return
     * @throws GlobalException
     */
    Post getPreviousInfo(Integer id) throws GlobalException;

    /**
     * 获取下一页文章信息
     * @param id
     * @return
     * @throws GlobalException
     */
    Post getNextInfo(Integer id) throws GlobalException;

    /**
     * 点赞文章
     *
     * @param ipAddr
     * @param postId
     * @return
     * @throws GlobalException
     */
    int praisePost(String ipAddr, Integer postId) throws GlobalException;

    /**
     * 获取文章数量
     * @param categoryId
     * @return
     * @throws GlobalException
     */
    Integer getPostNumByCategoryId(Integer categoryId) throws GlobalException;

    /**
     * 根据分类名称获取文章列表
     * @param categoryName
     * @param pageNum
     * @param pageSize
     * @return
     * @throws GlobalException
     */
    List<Post> listPostsByCategoryName(String categoryName, Integer pageNum, Integer pageSize) throws GlobalException;

    /**
     * 根据标签名称获取文章列表
     * @param tagName
     * @param pageNum
     * @param pageSize
     * @return
     * @throws GlobalException
     */
    List<Post> listPostsByTagName(String tagName, Integer pageNum, Integer pageSize) throws GlobalException;

    /**
     * 查询指定文章
     * @return
     * @throws GlobalException
     */
    List<Post> findTopList() throws GlobalException;

    /**
     * 获取 contentHtml 为空的文章列表（定时器任务专用）
     * @return
     * @throws GlobalException
     */
    List<Post> listEmptyHtml() throws GlobalException;
}
