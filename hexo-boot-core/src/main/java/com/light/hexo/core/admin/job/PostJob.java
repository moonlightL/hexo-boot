package com.light.hexo.core.admin.job;

import com.light.hexo.common.util.MarkdownUtil;
import com.light.hexo.mapper.model.Post;
import com.light.hexo.mapper.model.PostTask;
import com.light.hexo.core.admin.service.PostService;
import com.light.hexo.core.admin.service.PostTaskService;
import com.light.hexo.common.exception.GlobalException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

    private ExecutorService executorService = Executors.newFixedThreadPool(2);

    /**
     * 扫描 contentHtml 内容为空的文章（兼容老版本）
     */
//    @Scheduled(cron = "0 0/1 * * * ?")
    public void checkContentHtml() {

        List<Post> postList = this.postService.listEmptyHtml();
        if (CollectionUtils.isEmpty(postList)) {
            return;
        }

        for (Post post : postList) {
            this.executorService.submit(() -> {
                Post tmp = new Post();
                tmp.setId(post.getId());
                tmp.setContentHtml(MarkdownUtil.md2html(post.getContent())).setUpdateTime(LocalDateTime.now());
                this.postService.updateModel(tmp);
            });
        }
    }

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
                log.error("checkPostTask 异常: {} ", e.getMessage());
            }

            postTask.setState(1).setUpdateTime(date);
            this.postTaskService.updateModel(postTask);
        }
    }
}
