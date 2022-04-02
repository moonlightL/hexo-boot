package com.light.hexo.business.admin.service.impl;

import cn.hutool.core.net.URLDecoder;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
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

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
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
    public Integer getCommentNum(Integer commentType) {
        Example example = new Example(Comment.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("commentType", commentType);
        return this.getBaseMapper().selectCountByExample(example);
    }

    @Override
    public void removeCommentBatch(List<String> idStrList) throws GlobalException {
        List<Integer> idList = idStrList.stream().map(Integer::valueOf).collect(Collectors.toList());

        Example example = new Example(Comment.class);
        example.createCriteria().andIn("id", idList);
        List<Comment> commentList = this.getBaseMapper().selectByExample(example);
        if (commentList.isEmpty()) {
            return;
        }

        for (Comment comment : commentList) {
            // 逻辑删除
            Integer id = comment.getId();
            this.commentMapper.updateDelStatus(id);

            // 减评论数
            Post post = this.postService.getSimpleInfo(comment.getPage());
            if (post != null) {
                this.eventPublisher.emit(new PostEvent(post.getId(), PostEvent.Type.COMMENT_MINUS));
            }
        }

        EhcacheUtil.clearByCacheName("commentCache");
    }

    @Override
    public PageInfo<Comment> findPage(BaseRequest<Comment> request) throws GlobalException {
        PageInfo<Comment> pageInfo = super.findPage(request);
        List<Comment> list = pageInfo.getList();
        if (CollectionUtils.isEmpty(list)) {
            return pageInfo;
        }

        // 查询父级留言
        Map<Integer, Comment> parentMap = new HashMap<>();
        List<Integer> pidList = list.stream().map(Comment::getPId).filter(i -> i > 0).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(pidList)) {
            Example example = Example.builder(Comment.class).where(Sqls.custom().andIn("id", pidList)).build();
            List<Comment> parentList = this.getBaseMapper().selectByExample(example);
            parentMap = parentList.stream().collect(Collectors.toMap(Comment::getId, Function.identity(), (k1, k2)->k1));
        }

        List<Blacklist> blacklistList = this.blacklistService.findAll();
        List<String> ipList = blacklistList.stream().map(Blacklist::getIpAddress).collect(Collectors.toList());

        for (Comment comment : list) {
            // 父级评论
            Comment parent = parentMap.get(comment.getPId());
            if (parent != null) {
                comment.setParent(parent);
            }
            // 检测 ip 是否进入黑名单
            comment.setBlacklist(ipList.contains(comment.getIpAddress()));
        }

        return pageInfo;
    }

    @Override
    public void replyByAdmin(Comment commentReply) throws GlobalException {

        Comment parent = super.findById(commentReply.getPId());
        if (parent == null || parent.getDelete()) {
            return;
        }

        String homePage = this.configService.getConfigValue(ConfigEnum.HOME_PAGE.getName());
        commentReply.setHomePage(homePage)
                    .setBannerId(parent.getBannerId() > 0 ? parent.getBannerId() : parent.getId())
                    .setSourceNickname(parent.getNickname())
                    .setPage(parent.getPage())
                    .setCommentType(parent.getCommentType())
                    .setBlogger(true)
                    .setDelete(false);
        this.saveModel(commentReply);

        Post post = this.postService.getSimpleInfo(parent.getPage());
        if (post != null) {
            this.eventPublisher.emit(new PostEvent(post.getId(), PostEvent.Type.COMMENT_ADD));
        }

        if (StringUtils.isBlank(parent.getEmail())) {
            return;
        }

        this.emailService.sendEmail(parent.getEmail(), "博主回复你的评论", commentReply.getContent());
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
        String decodePage = URLDecoder.decode(page, Charset.defaultCharset());

        if (isSingleRow) {
            Example example = new Example(Comment.class);
            example.createCriteria().andEqualTo("page", decodePage)
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
            example.createCriteria().andEqualTo("page", decodePage)
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
            Example replyExample = Example.builder(Comment.class).where(Sqls.custom().andIn("bannerId", pidList)).build();
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
    public Integer getCommentNumByBannerId(String page) {
        String decodePage = URLDecoder.decode(page, Charset.defaultCharset());

        Example example = new Example(Comment.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("delete", false)
                .andEqualTo("bannerId", 0);
        if (StringUtils.isNotBlank(page)) {
            criteria.andEqualTo("page", decodePage);
        }
        return this.getBaseMapper().selectCountByExample(example);
    }

    @Override
    public void saveCommentByIndex(Comment comment) {

        String page = comment.getPage();
        String decodePage = URLDecoder.decode(page, Charset.defaultCharset());

        Nav nav = this.navService.findByLink(decodePage);
        if (nav == null || nav.getId() == null) {
            // 查询为空，说明文章详情
            Post post = this.postService.getSimpleInfo(decodePage);
            if (post == null) {
                ExceptionUtil.throwEx(HexoExceptionEnum.ERROR_POST_COMMENT_PAGE_NOT_EXIST);
            }

            if (!post.getComment()) {
                ExceptionUtil.throwEx(HexoExceptionEnum.ERROR_NOT_SUPPORT_COMMENT);
            }
        }

        comment.setPage(decodePage);
        comment.setCommentType(nav.getId() != null ? 2 : 1);

        String toEmail = "", subject = "";

        if (comment.getPId() == null || comment.getPId() == 0) {
            toEmail = this.configService.getConfigValue(ConfigEnum.EMAIL.getName());
            subject = comment.getNickname() + "评论了你的文章";
        } else {
            Comment parent = super.findById(comment.getPId());
            if (parent != null && !parent.getDelete()) {
                comment.setBannerId(parent.getBannerId() > 0 ? parent.getBannerId() : parent.getId()).setSourceNickname(parent.getNickname());
                if (StringUtils.isNotBlank(parent.getEmail())) {
                    toEmail = parent.getEmail();
                    subject = comment.getNickname() + "回复你的评论";
                }
            }
        }

        this.saveModel(comment);

        if (StringUtils.isNotBlank(toEmail) && StringUtils.isNotBlank(subject)) {
            this.emailService.sendEmail(toEmail, subject, comment.getContent());

            Post post = this.postService.getSimpleInfo(comment.getPage());
            if (post != null) {
                this.eventPublisher.emit(new PostEvent(post.getId(), PostEvent.Type.COMMENT_ADD));
                this.eventPublisher.emit(new MessageEvent(this, comment.getNickname() + "对你的文章评论了", MessageEvent.Type.POST_COMMENT));
            } else {
                this.eventPublisher.emit(new MessageEvent(this, comment.getNickname() + "在你的博客留言了", MessageEvent.Type.GUEST_BOOK));
            }
        }
    }
}
