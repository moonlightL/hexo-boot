package com.light.hexo.core.admin.job;

import com.light.hexo.common.exception.GlobalException;
import com.light.hexo.core.admin.service.PostService;
import com.light.hexo.core.admin.service.PostTaskService;
import com.light.hexo.mapper.model.PostTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @Author MoonlightL
 * @ClassName: PostJob
 * @ProjectName hexo-boot
 * @Description: 文章定时器
 * @DateTime 2021/4/27 17:03
 */
@Component
@Slf4j
public class PostJob {

    @Autowired
    private PostService postService;

    @Autowired
    private PostTaskService postTaskService;

    /**
     * 定时发布文章
     */
    @Scheduled(cron = "0 0/1 * * * ?")
    public void checkPostTask() {

        List<PostTask> postTaskList = this.postTaskService.listPostTasks(0);
        if (CollectionUtils.isEmpty(postTaskList)) {
            return;
        }

        LocalDateTime date = LocalDateTime.now();
        for (PostTask postTask : postTaskList) {
            try {
                Integer postId = postTask.getPostId();
                this.postService.publishPost(postId);
            } catch (GlobalException e) {
                log.error("checkPostTask 异常: {} ", e);
            }

            postTask.setState(1).setUpdateTime(date);
            this.postTaskService.updateModel(postTask);
        }
    }
}
