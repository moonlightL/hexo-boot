package com.light.hexo.business.admin.service.impl;

import com.light.hexo.business.admin.constant.ConfigEnum;
import com.light.hexo.business.admin.constant.HexoExceptionEnum;
import com.light.hexo.business.admin.mapper.UserMapper;
import com.light.hexo.business.admin.model.User;
import com.light.hexo.business.admin.service.ConfigService;
import com.light.hexo.business.admin.service.UserService;
import com.light.hexo.common.base.BaseMapper;
import com.light.hexo.common.base.BaseRequest;
import com.light.hexo.common.base.BaseServiceImpl;
import com.light.hexo.common.constant.HexoConstant;
import com.light.hexo.common.exception.GlobalException;
import com.light.hexo.common.exception.GlobalExceptionEnum;
import com.light.hexo.common.model.UserRequest;
import com.light.hexo.common.util.ExceptionUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.ResourceUtils;
import tk.mybatis.mapper.entity.Example;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * @Author MoonlightL
 * @ClassName: UserServiceImpl
 * @ProjectName hexo-boot
 * @Description: 用户 Service 实现
 * @DateTime 2020/7/30 10:32
 */
@Service
public class UserServiceImpl extends BaseServiceImpl<User> implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ConfigService configService;

    @Override
    public BaseMapper<User> getBaseMapper() {
        return userMapper;
    }

    @Override
    protected Example getExample(BaseRequest request) {

        UserRequest userRequest = (UserRequest) request;
        Example example = new Example(User.class);
        Example.Criteria criteria = example.createCriteria();

        String nickname = userRequest.getNickname();
        if (StringUtils.isNotBlank(nickname)) {
            criteria.andLike("nickname", nickname + "%");
        }

        Boolean state = userRequest.getState();
        if (state != null) {
            criteria.andEqualTo("state", state);
        }

        example.orderBy("id").desc();
        return example;
    }

    @Override
    public User findUserByUsername(String username) throws GlobalException {
        Example example = new Example(User.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("username", username);
        return this.userMapper.selectOneByExample(example);
    }

    @Override
    public List<User> listUserByIdList(List<Integer> uidList) throws GlobalException {

        Example example = new Example(User.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("id", uidList);

        return this.userMapper.selectByExample(example);
    }

    @Override
    public void updateState(User model) throws GlobalException {

        User user = super.findById(model.getId());
        if (user != null && user.getRole().equals(1)) {
            ExceptionUtil.throwEx(HexoExceptionEnum.ERROR_CAN_NOT_CLOSE_ADMIN);
        }

        super.updateModel(model);
    }

    @Override
    public int getUserNum() throws GlobalException {
        Example example = new Example(User.class);
        return this.getBaseMapper().selectCountByExample(example);
    }

    @Override
    public User checkUser(String nickname, String email, String avatar) throws GlobalException {

        Example example = new Example(User.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("email", email);
        User user = this.userMapper.selectOneByExample(example);
        if (user == null) {
            user = new User();
            user.setUsername(email)
                .setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()))
                .setEmail(email)
                .setNickname(nickname)
                .setAvatar(HexoConstant.COMMENT_USER_AVATAR_DIR + "/" + avatar)
                .setState(true)
                .setRole(2);
            super.saveModel(user);
        } else {
            user.setAvatar(HexoConstant.COMMENT_USER_AVATAR_DIR + "/" + avatar);
            if (!user.getNickname().equals(nickname)) {
                user.setNickname(nickname);
            }
            super.updateModel(user);
        }

        return user;
    }

    @Override
    public void removeUserBatch(List<String> idStrList, Integer userId) throws GlobalException {
        List<Integer> idList = idStrList.stream().map(Integer::valueOf).collect(Collectors.toList());

        if (idList.contains(1)) {
            ExceptionUtil.throwEx(GlobalExceptionEnum.ERROR_CAN_NOT_DELETE_RESOURCE);
        }

        if (idList.contains(userId)) {
            ExceptionUtil.throwEx(HexoExceptionEnum.ERROR_CAN_NOT_REMOVE_SELF);
        }

       super.removeBatch(idList);
    }

    @Override
    public void updateInfo(User user) throws GlobalException {
        super.updateModel(user);

        String nickname = user.getNickname();
        String email = user.getEmail();

        Map<String, String> configMap = new HashMap<>();
        configMap.put(ConfigEnum.EMAIL.getName(), email);
        configMap.put(ConfigEnum.BLOG_AUTHOR.getName(), nickname);
        this.configService.saveConfig(configMap);

    }
}
