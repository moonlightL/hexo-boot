package com.light.hexo.business.admin.task;

import com.light.hexo.business.admin.model.Post;
import com.light.hexo.business.admin.service.PostService;
import com.light.hexo.common.util.MarkdownUtil;
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
 * @ClassName: PostTask
 * @ProjectName hexo-boot
 * @Description: 文章定时器
 * @DateTime 2021/4/27 17:03
 */
@Component
@Slf4j
public class PostTask {

    @Autowired
    private PostService postService;

    private ExecutorService executorService = Executors.newFixedThreadPool(2);

    /**
     * 扫描 contentHtml 内容为空的文章（兼容老版本）
     */
    @Scheduled(cron = "0 0/1 * * * ?")
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
}
