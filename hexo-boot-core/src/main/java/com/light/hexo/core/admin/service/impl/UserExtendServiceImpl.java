package com.light.hexo.core.admin.service.impl;

import com.light.hexo.common.base.BaseServiceImpl;
import com.light.hexo.common.component.event.BaseEvent;
import com.light.hexo.common.component.event.EventEnum;
import com.light.hexo.common.component.event.EventPublisher;
import com.light.hexo.common.component.event.EventService;
import com.light.hexo.common.event.AboutEvent;
import com.light.hexo.common.util.SpringContextUtil;
import com.light.hexo.mapper.mapper.UserExtendMapper;
import com.light.hexo.mapper.base.BaseMapper;
import com.light.hexo.mapper.model.UserExtend;
import com.light.hexo.core.admin.service.UserExtendService;
import com.light.hexo.common.base.BaseRequest;
import com.light.hexo.common.exception.GlobalException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.ServletContext;
import java.util.List;

/**
 * @Author MoonlightL
 * @ClassName: UserExtendServiceImpl
 * @ProjectName hexo-boot
 * @Description: 用户信息扩展 Service 实现
 * @DateTime 2020/9/28 14:02
 */
@Service
@Slf4j
public class UserExtendServiceImpl extends BaseServiceImpl<UserExtend> implements UserExtendService, EventService {

    @Autowired
    private UserExtendMapper userExtendMapper;

    @Autowired
    @Lazy
    private EventPublisher eventPublisher;

    @Override
    public BaseMapper<UserExtend> getBaseMapper() {
        return this.userExtendMapper;
    }

    @Override
    protected Example getExample(BaseRequest request) {
        return null;
    }

    @Override
    public UserExtend getUserExtendByUid(Integer uid) throws GlobalException {
        Example example = new Example(UserExtend.class);
        example.createCriteria().andEqualTo("uid", uid);
        return this.getBaseMapper().selectOneByExample(example);
    }

    @Override
    public void saveUserExtend(Integer uid, String desc) throws GlobalException {
        UserExtend userExtend = this.getUserExtendByUid(uid);
        if (userExtend == null) {
            userExtend = new UserExtend();
            userExtend.setUid(uid).setDescr(desc);
            super.saveModel(userExtend);
        } else {
            userExtend.setDescr(desc);
            super.updateModel(userExtend);
        }

        this.eventPublisher.emit(new AboutEvent(this));
    }

    @Override
    public UserExtend getBloggerInfo() throws GlobalException {

        List<UserExtend> extendList = super.findAll();
        if (!extendList.isEmpty()) {
            return extendList.get(0);
        }

        return new UserExtend();
    }

    @Override
    public void dealWithEvent(BaseEvent event) {

        WebApplicationContext webApplicationContext = (WebApplicationContext) SpringContextUtil.applicationContext;
        ServletContext servletContext = webApplicationContext.getServletContext();
        if (servletContext == null) {
            log.info("===========PostService dealWithEvent 获取 servletContext 为空============");
            return;
        }

        // 关于
        UserExtend bloggerInfo = this.getBloggerInfo();
        servletContext.setAttribute("aboutContent", StringUtils.isBlank(bloggerInfo.getDescr()) ? "" : bloggerInfo.getDescr());
    }

    @Override
    public String getCode() {
        return EventEnum.ABOUT.getType();
    }
}
