package com.light.hexo.business.admin.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.light.hexo.business.admin.component.EmailService;
import com.light.hexo.business.admin.constant.ConfigEnum;
import com.light.hexo.business.admin.constant.HexoExceptionEnum;
import com.light.hexo.business.admin.mapper.PostCommentMapper;
import com.light.hexo.business.admin.model.*;
import com.light.hexo.business.admin.model.event.MessageEvent;
import com.light.hexo.business.admin.model.event.PostEvent;
import com.light.hexo.business.admin.service.*;
import com.light.hexo.common.base.BaseMapper;
import com.light.hexo.common.base.BaseRequest;
import com.light.hexo.common.base.BaseServiceImpl;
import com.light.hexo.common.component.event.EventPublisher;
import com.light.hexo.common.exception.GlobalException;
import com.light.hexo.common.model.PostCommentRequest;
import com.light.hexo.common.util.DateUtil;
import com.light.hexo.common.util.EhcacheUtil;
import com.light.hexo.common.util.ExceptionUtil;
import com.light.hexo.common.util.IpUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.util.Sqls;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Author MoonlightL
 * @ClassName: PostCommentServiceImpl
 * @ProjectName hexo-boot
 * @Description: 文章评论 Service 实现
 * @DateTime 2020/8/21 11:51
 */
@CacheConfig(cacheNames = "postCommentCache")
@Service
public class PostCommentServiceImpl extends BaseServiceImpl<PostComment> implements PostCommentService {

    @Autowired
    private PostCommentMapper postCommentMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private PostService postService;

    @Autowired
    private BlacklistService blacklistService;

    @Autowired
    private EventPublisher eventPublisher;

    @Autowired
    private EmailService emailService;

    @Autowired
    private ConfigService configService;

    @Override
    public BaseMapper<PostComment> getBaseMapper() {
        return this.postCommentMapper;
    }

    @Override
    protected Example getExample(BaseRequest request) {
        PostCommentRequest postCommentRequest = (PostCommentRequest) request;
        Example example = new Example(PostComment.class);
        Example.Criteria criteria = example.createCriteria();

        Boolean delete = postCommentRequest.getDelete();
        if (delete != null) {
            criteria.andEqualTo("delete", delete);
        }

        String nickname = postCommentRequest.getNickname();
        if (StringUtils.isNotBlank(nickname)) {
            criteria.andLike("nickname", "%" + nickname.trim() + "%");
        }

        String ipAddress = postCommentRequest.getIpAddress();
        if (StringUtils.isNotBlank(ipAddress)) {
            criteria.andLike("ipAddress", "%" + ipAddress.trim() + "%");
        }

        String title = postCommentRequest.getTitle();
        if (StringUtils.isNotBlank(title)) {
            criteria.andLike("title", "%" + title.trim() + "%");
        }

        example.orderBy("id").desc();
        return example;
    }

    @Override
    public int saveModel(PostComment postComment) throws GlobalException {
        int num = super.saveModel(postComment);
        if (num > 0) {
            EhcacheUtil.clearByCacheName("postCommentCache");
        }
        return num;
    }

    @Override
    public PageInfo<PostComment> findPage(BaseRequest<PostComment> request) throws GlobalException {
        PageInfo<PostComment> pageInfo = super.findPage(request);
        List<PostComment> list = pageInfo.getList();
        if (CollectionUtils.isEmpty(list)) {
            return pageInfo;
        }

        // 查询留言用户
        List<Integer> uidList = list.stream().map(PostComment::getUserId).collect(Collectors.toList());
        List<User> userList = this.userService.listUserByIdList(uidList);
        Map<Integer, User> userMap = userList.stream().collect(Collectors.toMap(User::getId, Function.identity(), (k1, k2)->k1));

        // 查询父级留言
        Map<Integer, PostComment> parentMap = new HashMap<>();
        List<Integer> pidList = list.stream().map(PostComment::getPId).filter(i -> i > 0).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(pidList)) {
            Example example = Example.builder(PostComment.class).where(Sqls.custom().andIn("id", pidList)).build();
            List<PostComment> parentList = this.getBaseMapper().selectByExample(example);
            parentMap = parentList.stream().collect(Collectors.toMap(PostComment::getId, Function.identity(), (k1, k2)->k1));
        }

        // 查询文章
        List<Integer> postIdList = list.stream().map(PostComment::getPostId).collect(Collectors.toList());
        List<Post> postList = this.postService.listPostByIdList(postIdList);
        Map<Integer, Post> postMap = postList.stream().collect(Collectors.toMap(Post::getId, Function.identity(), (k1, k2)->k1));

        List<Blacklist> blacklistList = this.blacklistService.findAll();
        List<String> ipList = blacklistList.stream().map(Blacklist::getIpAddress).collect(Collectors.toList());

        for (PostComment postComment : list) {
            User user = userMap.get(postComment.getUserId());
            postComment.setAvatar(user == null ? "" : user.getAvatar());
            // 父级评论
            PostComment parent = parentMap.get(postComment.getPId());
            if (parent != null) {
                postComment.setParent(parent);
            }
            // 文章链接
            Post post = postMap.get(postComment.getPostId());
            if (post != null) {
                postComment.setPostLink(post.getLink());
            }

            // 检测 ip 是否进入黑名单
            postComment.setBlacklist(ipList.contains(postComment.getIpAddress()));
        }

        return pageInfo;
    }

    @Override
    public void savePostComment(PostComment postComment) throws GlobalException {
        Integer postId = postComment.getPostId();
        Post post = this.postService.findById(postId);
        if (post == null) {
            ExceptionUtil.throwEx(HexoExceptionEnum.ERROR_POST_NOT_EXIST);
        }

        postComment.setTitle(post.getTitle().trim());
        this.saveModel(postComment);

        this.eventPublisher.emit(new PostEvent(postId, PostEvent.Type.COMMENT_ADD));
    }

    @Override
    public void removePostCommentBatch(List<String> idStrList) throws GlobalException {
        List<Integer> idList = idStrList.stream().map(Integer::valueOf).collect(Collectors.toList());

        Example example = new Example(PostComment.class);
        example.createCriteria().andIn("id", idList);
        List<PostComment> postCommentList = this.getBaseMapper().selectByExample(example);
        if (postCommentList.isEmpty()) {
            return;
        }

        for (PostComment postComment : postCommentList) {
            // 逻辑删除
            Integer id = postComment.getId();
            this.postCommentMapper.updateDelStatus(id);

            // 减评论数
            Integer postId = postComment.getPostId();
            this.eventPublisher.emit(new PostEvent(postId, PostEvent.Type.COMMENT_MINUS));
        }

        EhcacheUtil.clearByCacheName("postCommentCache");
    }

    @Override
    public void replyByAdmin(PostComment postComment) throws GlobalException {

        PostComment parent = super.findById(postComment.getPId());
        if (parent == null || parent.getDelete()) {
            return;
        }

        postComment.setPostId(parent.getPostId())
                   .setTitle(parent.getTitle())
                   .setBannerId(parent.getBannerId() > 0 ? parent.getBannerId() : parent.getId())
                   .setSourceNickname(parent.getNickname())
                   .setDelete(false);
        this.saveModel(postComment);

        this.eventPublisher.emit(new PostEvent(parent.getPostId(), PostEvent.Type.COMMENT_ADD));

        Integer userId = parent.getUserId();
        User visitor = this.userService.findById(userId);
        if (visitor == null) {
            return;
        }

        this.emailService.sendEmail(visitor.getEmail(), "博主回复你的评论", postComment.getContent());
    }

    @Override
    public void addBlacklist(PostComment postComment, String ipAddr) throws GlobalException {
        Integer id = postComment.getId();
        PostComment db = super.findById(id);
        if (db == null) {
            ExceptionUtil.throwEx(HexoExceptionEnum.ERROR_POST_COMMENT_NOT_EXIST);
        }

        String ipAddress = db.getIpAddress();
        if (ipAddress.equals(ipAddr)) {
            ExceptionUtil.throwEx(HexoExceptionEnum.ERROR_IP_ADDRESS_TO_BLACK_LIST);
        }

        boolean exist = this.blacklistService.isBlacklist(ipAddress);
        if (exist) {
            return;
        }

        this.blacklistService.saveBlacklist(ipAddress, "通过评论列表页加入黑名单");
    }

    @Override
    public int getPostCommentNum(Integer postId) throws GlobalException {
        Example example = new Example(PostComment.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("delete", false);
        if (postId != null) {
            criteria.andEqualTo("postId", postId);
        }
        return this.getBaseMapper().selectCountByExample(example);
    }

    @Override
    public List<PostComment> listCommentByPostId(Integer postId, Integer pageNum, Integer pageSize) throws GlobalException {

        Example example = new Example(PostComment.class);
        example.createCriteria().andEqualTo("postId", postId)
                                .andEqualTo("delete", 0);
        example.orderBy("createTime").desc();

        PageHelper.startPage(pageNum, pageSize);
        List<PostComment> postCommentList = this.getBaseMapper().selectByExample(example);

        if (CollectionUtils.isEmpty(postCommentList)) {
            return new ArrayList<>();
        }

        // 父级评论
        List<Integer> pidList = postCommentList.stream().map(PostComment::getPId).collect(Collectors.toList());
        Example parentExample = Example.builder(PostComment.class).where(Sqls.custom().andIn("id", pidList)).build();
        List<PostComment> parentList = this.getBaseMapper().selectByExample(parentExample);
        Map<Integer, PostComment> parentMap = parentList.stream().collect(Collectors.toMap(PostComment::getId, Function.identity(), (k1, k2)->k1));

        for (PostComment postComment : postCommentList) {
            // 判断是否为博主
            postComment.setIpInfo(IpUtil.getProvinceAndCity(postComment.getIpAddress()));
            postComment.setIpAddress("");
            // 父级评论
            PostComment parent = parentMap.get(postComment.getPId());
            if (parent != null) {
                parent.setIpAddress("");
                postComment.setParent(parent);
            }
            postComment.setTimeDesc(DateUtil.timeDesc(postComment.getCreateTime()));
        }

        return postCommentList;
    }

    @Override
    public List<PostComment> getCommentListByPostId(Integer postId, Integer pageNum, Integer pageSize) throws GlobalException {

        Example example = new Example(PostComment.class);
        example.createCriteria().andEqualTo("postId", postId)
                                .andEqualTo("delete", 0)
                                .andEqualTo("bannerId", 0);
        example.orderBy("createTime").desc();

        PageHelper.startPage(pageNum, pageSize);
        List<PostComment> parentList = this.getBaseMapper().selectByExample(example);

        if (CollectionUtils.isEmpty(parentList)) {
            return new ArrayList<>();
        }

        // 查询子级回复列表
        List<Integer> pidList = parentList.stream().map(PostComment::getId).collect(Collectors.toList());
        Example replyExample = Example.builder(PostComment.class).where(Sqls.custom().andIn("bannerId", pidList)).build();
        List<PostComment> replyList = this.getBaseMapper().selectByExample(replyExample);
        Map<Integer, List<PostComment>> replyMap = replyList.stream().collect(Collectors.groupingBy(PostComment::getBannerId));

        for (PostComment postComment : parentList) {
            postComment.setIpInfo(IpUtil.getProvinceAndCity(postComment.getIpAddress()));
            postComment.setIpAddress("");
            // 子级评论
            List<PostComment> children = replyMap.get(postComment.getId());
            if (!CollectionUtils.isEmpty(children)) {
                children.forEach(i -> {
                    i.setIpInfo(IpUtil.getProvinceAndCity(i.getIpAddress()));
                    i.setIpAddress("");
                    i.setTimeDesc(DateUtil.timeDesc(i.getCreateTime()));
                });
                postComment.setReplyList(children);
            }
            postComment.setTimeDesc(DateUtil.timeDesc(postComment.getCreateTime()));
        }

        return parentList;
    }

    @Override
    public void saveCommentByIndex(PostComment postComment) throws GlobalException {

        Integer postId = postComment.getPostId();
        Post post = this.postService.findById(postId);
        if (post == null) {
            ExceptionUtil.throwEx(HexoExceptionEnum.ERROR_POST_NOT_EXIST);
        }

        if (!post.getComment()) {
            ExceptionUtil.throwEx(HexoExceptionEnum.ERROR_NOT_SUPPORT_COMMENT);
        }

        String toEmail = "", subject = "";
        User commentUser = this.userService.checkUser(postComment.getNickname(), postComment.getEmail(), postComment.getAvatar());

        PostComment parent = super.findById(postComment.getPId());
        if (parent != null && !parent.getDelete()) {
            postComment.setBannerId(parent.getBannerId() > 0 ? parent.getBannerId() : parent.getId()).setSourceNickname(parent.getNickname());

            User user = this.userService.findById(parent.getUserId());
            if (user != null) {
                toEmail = user.getEmail();
                subject = commentUser.getNickname() + "回复你的评论";
            }
        } else {
            toEmail = this.configService.getConfigValue(ConfigEnum.EMAIL.getName());
            subject = commentUser.getNickname() + "评论了你的文章";
        }

        postComment.setUserId(commentUser.getId())
                   .setNickname(commentUser.getNickname())
                   .setTitle(post.getTitle().trim())
                   .setAvatar(commentUser.getAvatar());
        this.saveModel(postComment);

        this.eventPublisher.emit(new PostEvent( postComment.getPostId(), PostEvent.Type.COMMENT_ADD));
        this.eventPublisher.emit(new MessageEvent(this, postComment.getNickname() + "评论了你的文章", MessageEvent.Type.POST_COMMENT));

        this.emailService.sendEmail(toEmail, subject, postComment.getContent());
    }
}
