package com.light.hexo.business.admin.service;

import com.light.hexo.business.admin.model.Comment;
import com.light.hexo.common.base.BaseService;
import com.light.hexo.common.exception.GlobalException;

import java.util.List;

/**
 * @Author MoonlightL
 * @ClassName: CommentService
 * @ProjectName hexo-boot
 * @Description: 评论 Service
 * @DateTime 2022/1/27, 0027 13:41
 */
public interface CommentService extends BaseService<Comment> {

    /**
     * 获取评论数
     * @param commentType  类型 1：文章 2：留言
     * @return
     */
    Integer getCommentNum(Integer commentType);

    /**
     * 批量删除评论
     * @param idStrList
     * @throws GlobalException
     */
    void removeCommentBatch(List<String> idStrList) throws GlobalException;

    /**
     * 回复评论
     * @param comment
     * @throws GlobalException
     */
    void replyByAdmin(Comment comment) throws GlobalException;

    /**
     * 加入黑名单
     * @param toDoModel
     * @param ipAddr
     */
    void addBlacklist(Comment toDoModel, String ipAddr);

    /**
     * 获取文章评论列表
     * @param page
     * @param pageNum
     * @param pageSize
     * @param isSingleRow
     * @return
     */
    List<Comment> listCommentByPage(String page, Integer pageNum, int pageSize, boolean isSingleRow);

    /**
     * 查询评论数
     * @param page
     * @return
     */
    Integer getCommentNumByBannerId(String page);

    /**
     * 用户评论
     * @param comment
     */
    void saveCommentByIndex(Comment comment);
}
