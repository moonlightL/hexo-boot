package com.light.hexo.business.admin.service;

import com.light.hexo.business.admin.model.PostTask;
import com.light.hexo.common.base.BaseService;
import com.light.hexo.common.exception.GlobalException;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Author MoonlightL
 * @ClassName: PostTaskService
 * @ProjectName hexo-boot
 * @Description: 文章任务 Service
 * @DateTime 2021/7/12 9:47
 */
public interface PostTaskService extends BaseService<PostTask> {

    /**
     * 获取文章任务列表
     * @param state
     * @return
     */
    List<PostTask> listPostTasks(int state) throws GlobalException;

    /**
     * 保存文章任务
     * @param postId
     * @param jobTime
     * @throws GlobalException
     */
    void savePostTask(Integer postId, LocalDateTime jobTime) throws GlobalException;

    /**
     * 获取文章任务
     * @param postId
     * @return
     */
    PostTask getPostTask(Integer postId) throws GlobalException;
}
