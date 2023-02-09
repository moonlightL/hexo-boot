package com.light.hexo.core.admin.service.impl;

import com.light.hexo.common.base.BaseServiceImpl;
import com.light.hexo.mapper.mapper.UserExtendMapper;
import com.light.hexo.mapper.base.BaseMapper;
import com.light.hexo.mapper.model.UserExtend;
import com.light.hexo.core.admin.service.UserExtendService;
import com.light.hexo.common.base.BaseRequest;
import com.light.hexo.common.exception.GlobalException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * @Author MoonlightL
 * @ClassName: UserExtendServiceImpl
 * @ProjectName hexo-boot
 * @Description: 用户信息扩展 Service 实现
 * @DateTime 2020/9/28 14:02
 */
@Service
public class UserExtendServiceImpl extends BaseServiceImpl<UserExtend> implements UserExtendService {

    @Autowired
    private UserExtendMapper userExtendMapper;

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
    }

    @Override
    public UserExtend getBloggerInfo() throws GlobalException {

        List<UserExtend> extendList = super.findAll();
        if (!extendList.isEmpty()) {
            return extendList.get(0);
        }

        return new UserExtend();
    }
}
