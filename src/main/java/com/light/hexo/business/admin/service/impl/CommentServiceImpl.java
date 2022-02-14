package com.light.hexo.business.admin.service.impl;

import com.github.pagehelper.PageHelper;
import com.light.hexo.business.admin.component.EmailService;
import com.light.hexo.business.admin.constant.ConfigEnum;
import com.light.hexo.business.admin.constant.HexoExceptionEnum;
import com.light.hexo.business.admin.mapper.CommentMapper;
import com.light.hexo.business.admin.model.*;
import com.light.hexo.business.admin.model.event.MessageEvent;
import com.light.hexo.business.admin.model.event.PostEvent;
import com.light.hexo.business.admin.service.*;
import com.light.hexo.common.base.BaseMapper;
import com.light.hexo.common.base.BaseServiceImpl;
import com.light.hexo.common.base.BaseRequest;
import com.light.hexo.common.component.event.EventPublisher;
import com.light.hexo.common.exception.GlobalException;
import com.light.hexo.common.model.CommentRequest;
import com.light.hexo.common.util.DateUtil;
import com.light.hexo.common.util.EhcacheUtil;
import com.light.hexo.common.util.ExceptionUtil;
import com.light.hexo.common.util.IpUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.util.Sqls;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Author MoonlightL
 * @ClassName: CommentServiceImpl
 * @ProjectName hexo-boot
 * @Description: 评论 Service 实现
 * @DateTime 2022/1/27, 0027 13:43
 */
@Service
public class CommentServiceImpl extends BaseServiceImpl<Comment> implements CommentService {

    @Autowired
    private CommentMapper commentMapper;
    
    @Autowired
    private EventPublisher eventPublisher;

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private ConfigService configService;

    @Autowired
    private BlacklistService blacklistService;

    @Autowired
    private PostService postService;

    @Autowired
    private NavService navService;

    @Override
    public BaseMapper<Comment> getBaseMapper() {
        return this.commentMapper;
    }

    @Override
    protected Example getExample(BaseRequest request) {
        CommentRequest commentRequest = (CommentRequest) request;
        Example example = new Example(Comment.class);
        Example.Criteria criteria = example.createCriteria();

        Boolean delete = commentRequest.getDelete();
        if (delete != null) {
            criteria.andEqualTo("delete", delete);
        }

        String nickname = commentRequest.getNickname();
        if (StringUtils.isNotBlank(nickname)) {
            criteria.andLike("nickname", "%" + nickname.trim() + "%");
        }

        String ipAddress = commentRequest.getIpAddress();
        if (StringUtils.isNotBlank(ipAddress)) {
            criteria.andLike("ipAddress", "%" + ipAddress.trim() + "%");
        }

        example.orderBy("id").desc();
        return example;
    }

    @Override
    public int saveModel(Comment comment) throws GlobalException {
        int num = super.saveModel(comment);
        if (num > 0) {
            EhcacheUtil.clearByCacheName("commentCache");
        }
        return num;
    }

    @Override
    public void saveComment(Comment comment) throws GlobalException {
        this.saveModel(comment);

        Post post = this.postService.getSimpleInfo(comment.getPage());
        if (post != null) {
            this.eventPublisher.emit(new PostEvent(post.getId(), PostEvent.Type.COMMENT_ADD));
        }
    }

    @Override
    public void removeCommentBatch(List<String> idStrList) throws GlobalException {
        List<Integer> idList = idStrList.stream().map(Integer::valueOf).collect(Collectors.toList());

        Example example = new Example(PostComment.class);
        example.createCriteria().andIn("id", idList);
        List<Comment> postCommentList = this.getBaseMapper().selectByExample(example);
        if (postCommentList.isEmpty()) {
            return;
        }

        for (Comment comment : postCommentList) {
            // 逻辑删除
            Comment tmp = new Comment();
            tmp.setId(comment.getId());
            tmp.setDelete(true);
            this.getBaseMapper().updateByPrimaryKeySelective(tmp);

            // 减评论数
            Post post = this.postService.getSimpleInfo(comment.getPage());
            if (post != null) {
                this.eventPublisher.emit(new PostEvent(post.getId(), PostEvent.Type.COMMENT_MINUS));
            }
        }

        EhcacheUtil.clearByCacheName("commentCache");
    }

    @Override
    public void replyByAdmin(Comment comment) throws GlobalException {

        Comment parent = super.findById(comment.getPId());
        if (parent == null || parent.getDelete()) {
            return;
        }

        comment.setBannerId(parent.getBannerId() > 0 ? parent.getBannerId() : parent.getId())
               .setSourceNickname(parent.getNickname())
               .setDelete(false);
        this.saveModel(comment);

        Post post = this.postService.getSimpleInfo(comment.getPage());
        if (post != null) {
            this.eventPublisher.emit(new PostEvent(post.getId(), PostEvent.Type.COMMENT_ADD));
        }

        if (StringUtils.isBlank(comment.getEmail())) {
            return;
        }

        this.emailService.sendEmail(comment.getEmail(), "博主回复你的评论", comment.getContent());
    }

    @Override
    public void addBlacklist(Comment comment, String ipAddr) {

        Comment db = super.findById(comment.getId());
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

        this.blacklistService.saveBlacklist(ipAddress, "通过评论列表页加入黑名单", 2);
    }

    @Override
    public List<Comment> listCommentByPage(String page, Integer pageNum, int pageSize, boolean isSingleRow) {

        List<Comment> commentList;

        if (isSingleRow) {
            Example example = new Example(Comment.class);
            example.createCriteria().andEqualTo("page", page)
                    .andEqualTo("delete", 0);
            example.orderBy("createTime").desc();

            PageHelper.startPage(pageNum, pageSize);
            commentList = this.getBaseMapper().selectByExample(example);

            if (CollectionUtils.isEmpty(commentList)) {
                return new ArrayList<>();
            }

            // 父级评论
            List<Integer> pidList = commentList.stream().map(Comment::getPId).collect(Collectors.toList());
            Example parentExample = Example.builder(Comment.class).where(Sqls.custom().andIn("id", pidList)).build();
            List<Comment> parentList = this.getBaseMapper().selectByExample(parentExample);
            Map<Integer, Comment> parentMap = parentList.stream().collect(Collectors.toMap(Comment::getId, Function.identity(), (k1, k2)->k1));

            for (Comment comment : commentList) {
                // 判断是否为博主
                comment.setIpInfo(IpUtil.getProvinceAndCity(comment.getIpAddress()));
                comment.setIpAddress("");
                // 父级评论
                Comment parent = parentMap.get(comment.getPId());
                if (parent != null) {
                    parent.setIpAddress("");
                    comment.setParent(parent);
                }
                comment.setTimeDesc(DateUtil.timeDesc(comment.getCreateTime()));
            }
        } else {
            Example example = new Example(Comment.class);
            example.createCriteria().andEqualTo("page", page)
                    .andEqualTo("delete", 0)
                    .andEqualTo("bannerId", 0);
            example.orderBy("createTime").desc();

            PageHelper.startPage(pageNum, pageSize);
            commentList = this.getBaseMapper().selectByExample(example);

            if (CollectionUtils.isEmpty(commentList)) {
                return new ArrayList<>();
            }

            // 查询子级回复列表
            List<Integer> pidList = commentList.stream().map(Comment::getId).collect(Collectors.toList());
            Example replyExample = Example.builder(PostComment.class).where(Sqls.custom().andIn("bannerId", pidList)).build();
            List<Comment> replyList = this.getBaseMapper().selectByExample(replyExample);
            Map<Integer, List<Comment>> replyMap = replyList.stream().collect(Collectors.groupingBy(Comment::getBannerId));

            for (Comment comment : commentList) {
                comment.setIpInfo(IpUtil.getProvinceAndCity(comment.getIpAddress()));
                comment.setIpAddress("");
                // 子级评论
                List<Comment> children = replyMap.get(comment.getId());
                if (!CollectionUtils.isEmpty(children)) {
                    children.forEach(i -> {
                        i.setIpInfo(IpUtil.getProvinceAndCity(i.getIpAddress()));
                        i.setIpAddress("");
                        i.setTimeDesc(DateUtil.timeDesc(i.getCreateTime()));
                    });
                    comment.setReplyList(children);
                }
                comment.setTimeDesc(DateUtil.timeDesc(comment.getCreateTime()));
            }
        }

        return commentList;
    }

    @Override
    public Integer getCommentNum(String page) {
        Example example = new Example(Comment.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("delete", false);
        if (StringUtils.isNotBlank(page)) {
            criteria.andEqualTo("page", page);
        }
        return this.getBaseMapper().selectCountByExample(example);
    }

    @Override
    public void saveCommentByIndex(Comment comment) {

        String page = comment.getPage();

        // 查询判断是否为自定义页面
        Nav nav = this.navService.findByLink(page);
        if (nav == null) {
            // 查询为空，说明文章详情
            Post post = this.postService.getSimpleInfo(page);
            if (post == null) {
                ExceptionUtil.throwEx(HexoExceptionEnum.ERROR_POST_COMMENT_PAGE_NOT_EXIST);
            }

            if (!post.getComment()) {
                ExceptionUtil.throwEx(HexoExceptionEnum.ERROR_NOT_SUPPORT_COMMENT);
            }
        }

        String toEmail = "", subject = "";

        Comment parent = super.findById(comment.getPId());
        if (parent != null && !parent.getDelete()) {
            comment.setBannerId(parent.getBannerId() > 0 ? parent.getBannerId() : parent.getId()).setSourceNickname(parent.getNickname());
            if (StringUtils.isNotBlank(parent.getEmail())) {
                toEmail = parent.getEmail();
                subject = comment.getNickname() + "回复你的评论";
            }
        } else {
            toEmail = this.configService.getConfigValue(ConfigEnum.EMAIL.getName());
            subject = comment.getNickname() + "评论了你的文章";
        }

        this.saveModel(comment);

        Post post = this.postService.getSimpleInfo(comment.getPage());
        if (post != null) {
            this.eventPublisher.emit(new PostEvent(post.getId(), PostEvent.Type.COMMENT_ADD));
            this.eventPublisher.emit(new MessageEvent(this, comment.getNickname() + "评论了你的文章", MessageEvent.Type.POST_COMMENT));
        }

        this.emailService.sendEmail(toEmail, subject, comment.getContent());
    }
}
