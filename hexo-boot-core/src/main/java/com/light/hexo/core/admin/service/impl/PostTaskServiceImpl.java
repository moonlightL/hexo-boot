package com.light.hexo.core.admin.service.impl;

import com.light.hexo.common.base.BaseServiceImpl;
import com.light.hexo.mapper.mapper.PostTaskMapper;
import com.light.hexo.mapper.base.BaseMapper;
import com.light.hexo.mapper.model.PostTask;
import com.light.hexo.core.admin.service.PostTaskService;
import com.light.hexo.common.base.BaseRequest;
import com.light.hexo.common.exception.GlobalException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Author MoonlightL
 * @ClassName: PostTaskServiceImpl
 * @ProjectName hexo-boot
 * @Description: 文章任务 Service 实现
 * @DateTime 2021/7/12 9:48
 */
@Service
@Slf4j
public class PostTaskServiceImpl extends BaseServiceImpl<PostTask> implements PostTaskService {

    @Autowired
    private PostTaskMapper postTaskMapper;

    @Override
    public BaseMapper<PostTask> getBaseMapper() {
        return this.postTaskMapper;
    }

    @Override
    protected Example getExample(BaseRequest request) {
        return null;
    }

    @Override
    public List<PostTask> listPostTasks(int state) throws GlobalException {
        Example example = new Example(PostTask.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andLessThanOrEqualTo("jobTime", LocalDateTime.now())
                .andEqualTo("state", state);
        return this.getBaseMapper().selectByExample(example);
    }

    @Override
    public void savePostTask(Integer postId, LocalDateTime jobTime) throws GlobalException {
        PostTask postTask = this.getPostTask(postId);
        if (postTask == null) {
            postTask = new PostTask();
            postTask.setPostId(postId)
                    .setState(0)
                    .setJobTime(jobTime)
                    .setCreateTime(LocalDateTime.now())
                    .setUpdateTime(postTask.getCreateTime());
            this.getBaseMapper().insert(postTask);
        } else {
            postTask.setJobTime(jobTime).setUpdateTime(LocalDateTime.now());
            this.getBaseMapper().updateByPrimaryKey(postTask);
        }
    }

    @Override
    public PostTask getPostTask(Integer postId) throws GlobalException {
        Example example = new Example(PostTask.class);
        example.createCriteria().andEqualTo("postId", postId);
        return this.getBaseMapper().selectOneByExample(example);
    }
}
