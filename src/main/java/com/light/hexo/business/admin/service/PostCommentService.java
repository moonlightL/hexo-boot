package com.light.hexo.business.admin.service;

import com.light.hexo.business.admin.model.PostComment;
import com.light.hexo.common.base.BaseService;
import com.light.hexo.common.exception.GlobalException;

import java.time.LocalDate;
import java.util.List;

/**
 * @Author MoonlightL
 * @ClassName: PostCommentService
 * @ProjectName hexo-boot
 * @Description: 评论 Service
 * @DateTime 2020/8/21 11:51
 */
public interface PostCommentService extends BaseService<PostComment> {

    /**
     * 保存评论
     * @param postComment
     * @throws GlobalException
     */
    void savePostComment(PostComment postComment) throws GlobalException;

    /**
     * 批量删除评论
     * @param idStrList
     * @throws GlobalException
     */
    void removePostCommentBatch(List<String> idStrList) throws GlobalException;

    /**
     * 博主回复评论
     * @param postComment
     * @throws GlobalException
     */
    void replyByAdmin(PostComment postComment) throws GlobalException;

    /**
     * 加入黑名单
     * @param postComment
     * @param ipAddr
     * @throws GlobalException
     */
    void addBlacklist(PostComment postComment, String ipAddr) throws GlobalException;

    /**
     * 查询评论数
     * @return
     * @throws GlobalException
     */
    int getPostCommentNum() throws GlobalException;

    /**
     * 查询指定日期评论数
     * @param date
     * @return
     * @throws GlobalException
     */
    int getPostCommentNum(LocalDate date) throws GlobalException;

    // =========================== 以下为前端页面请求 ============================

    /**
     * 获取文章评论列表
     * @param postId
     * @param pageNum
     * @param pageSize
     * @return
     * @throws GlobalException
     */
    List<PostComment> listCommentByPostId(Integer postId, Integer pageNum, Integer pageSize) throws GlobalException;

    /**
     * 用户评论
     * @param comment
     * @throws GlobalException
     */
    void saveCommentByIndex(PostComment comment) throws GlobalException;

}
