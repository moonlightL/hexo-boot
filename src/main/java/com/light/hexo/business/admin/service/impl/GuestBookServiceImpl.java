package com.light.hexo.business.admin.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.light.hexo.business.admin.component.EmailService;
import com.light.hexo.business.admin.constant.ConfigEnum;
import com.light.hexo.business.admin.constant.HexoExceptionEnum;
import com.light.hexo.business.admin.mapper.GuestBookMapper;
import com.light.hexo.business.admin.model.*;
import com.light.hexo.business.admin.model.event.MessageEvent;
import com.light.hexo.business.admin.service.BlacklistService;
import com.light.hexo.business.admin.service.ConfigService;
import com.light.hexo.business.admin.service.GuestBookService;
import com.light.hexo.business.admin.service.UserService;
import com.light.hexo.common.base.BaseMapper;
import com.light.hexo.common.base.BaseRequest;
import com.light.hexo.common.base.BaseServiceImpl;
import com.light.hexo.common.component.event.EventPublisher;
import com.light.hexo.common.exception.GlobalException;
import com.light.hexo.common.model.GuestBookRequest;
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

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Author MoonlightL
 * @ClassName: GuestBookServiceImpl
 * @ProjectName hexo-boot
 * @Description: 留言 Service 实现
 * @DateTime 2020/9/4 14:56
 */
@CacheConfig(cacheNames = "guestBookCache")
@Service
public class GuestBookServiceImpl extends BaseServiceImpl<GuestBook> implements GuestBookService {

    @Autowired
    private GuestBookMapper guestBookMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private BlacklistService blacklistService;

    @Autowired
    private EventPublisher eventPublisher;

    @Autowired
    private EmailService emailService;

    @Autowired
    private ConfigService configService;

    @Override
    public BaseMapper<GuestBook> getBaseMapper() {
        return guestBookMapper;
    }

    @Override
    protected Example getExample(BaseRequest request) {
        // 获取查询参数
        GuestBookRequest guestBookRequest = (GuestBookRequest) request;
        Example example = new Example(GuestBook.class);
        Example.Criteria criteria = example.createCriteria();

        Boolean delete = guestBookRequest.getDelete();
        if (delete != null) {
            criteria.andEqualTo("delete", delete);
        }

        String nickname = guestBookRequest.getNickname();
        if (StringUtils.isNotBlank(nickname)) {
            criteria.andLike("nickname", nickname.trim() + "%");
        }

        String ipAddress = guestBookRequest.getIpAddress();
        if (StringUtils.isNotBlank(ipAddress)) {
            criteria.andLike("ipAddress", ipAddress + "%");
        }

        example.orderBy("id").desc();
        return example;
    }

    @Override
    public int saveModel(GuestBook guestBook) throws GlobalException {
        int num = super.saveModel(guestBook);
        if (num > 0) {
            EhcacheUtil.clearByCacheName("guestBookCache");
        }
        return num;
    }

    @Override
    public GuestBook findById(Serializable id) throws GlobalException {
        GuestBook guestBook = super.findById(id);
        User user = this.userService.findById(guestBook.getUserId());
        if (user != null) {
            guestBook.setNickname(user.getNickname());
            guestBook.setAvatar(user.getAvatar());
        }

        return guestBook;
    }

    @Override
    public PageInfo<GuestBook> findPage(BaseRequest<GuestBook> request) throws GlobalException {
        PageInfo<GuestBook> pageInfo = super.findPage(request);
        List<GuestBook> list = pageInfo.getList();
        if (CollectionUtils.isEmpty(list)) {
            return pageInfo;
        }

        // 查询留言用户
        List<Integer> uidList = list.stream().map(GuestBook::getUserId).collect(Collectors.toList());
        List<User> userList = this.userService.listUserByIdList(uidList);
        Map<Integer, User> userMap = userList.stream().collect(Collectors.toMap(User::getId, Function.identity(), (k1, k2)->k1));

        // 查询父级留言
        Map<Integer, GuestBook> parentMap = new HashMap<>();
        List<Integer> pidList = list.stream().map(GuestBook::getPId).filter(i -> i > 0).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(pidList)) {
            Example example = Example.builder(GuestBook.class).where(Sqls.custom().andIn("id", pidList)).build();
            List<GuestBook> parentList = this.getBaseMapper().selectByExample(example);
            parentMap = parentList.stream().collect(Collectors.toMap(GuestBook::getId, Function.identity(), (k1, k2)->k1));
        }

        List<Blacklist> blacklistList = this.blacklistService.findAll();
        List<String> ipList = blacklistList.stream().map(Blacklist::getIpAddress).collect(Collectors.toList());

        for (GuestBook guestBook : list) {
            User user = userMap.get(guestBook.getUserId());
            guestBook.setAvatar(user == null ? "" : user.getAvatar());
            GuestBook parent = parentMap.get(guestBook.getPId());
            if (parent != null) {
                guestBook.setParent(parent);
            }
            guestBook.setBlacklist(ipList.contains(guestBook.getIpAddress()));
        }

        return pageInfo;
    }

    @Override
    public void replyByAdmin(GuestBook guestBook) throws GlobalException {

        GuestBook parent = super.findById(guestBook.getPId());
        if (parent == null || parent.getDelete()) {
            return;
        }

        guestBook.setBannerId(parent.getBannerId() > 0 ? parent.getBannerId() : parent.getId())
                 .setSourceNickname(parent.getNickname())
                 .setDelete(false);
        this.saveModel(guestBook);

        Integer userId = parent.getUserId();
        User visitor = this.userService.findById(userId);
        if (visitor == null) {
            return;
        }

        this.emailService.sendEmail(visitor.getEmail(), "博主回复你的留言", guestBook.getContent());
    }

    @Override
    public void saveGuestBook(GuestBook guestBook) throws GlobalException {
        guestBook.setDelete(false);
        this.saveModel(guestBook);
    }

    @Override
    public void removeGuestBookBatch(List<String> idStrList) throws GlobalException {
        List<Integer> idList = idStrList.stream().map(Integer::valueOf).collect(Collectors.toList());
        // 逻辑删除
        this.guestBookMapper.updateDelStatusBatch(idList);
        EhcacheUtil.clearByCacheName("guestBookCache");
    }

    @Override
    public void addBlacklist(GuestBook guestBook, String ipAddr) throws GlobalException {
        Integer id = guestBook.getId();
        GuestBook db = super.findById(id);
        if (db == null) {
            ExceptionUtil.throwEx(HexoExceptionEnum.ERROR_GUEST_BOOK_NOT_EXIST);
        }

        String ipAddress = db.getIpAddress();
        if (ipAddress.equals(ipAddr)) {
            ExceptionUtil.throwEx(HexoExceptionEnum.ERROR_IP_ADDRESS_TO_BLACK_LIST);
        }

        boolean exist = this.blacklistService.isBlacklist(ipAddress);
        if (exist) {
            return;
        }

        this.blacklistService.saveBlacklist(ipAddr, "通过留言列表页加入黑名单");
    }

    @Override
    public int getGuestBookNum() throws GlobalException {
        Example example = new Example(GuestBook.class);
        example.createCriteria().andEqualTo("delete", false);
        return this.getBaseMapper().selectCountByExample(example);
    }

    @Override
    public int getGuestBookNum(LocalDate date) throws GlobalException {
        Example example = new Example(GuestBook.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andBetween("createTime", date + " 00:00:00", date + " 23:59:59");

        return this.getBaseMapper().selectCountByExample(example);
    }

    @Override
    public List<GuestBook> listGuestBookByIndex(Integer pageNum, Integer pageSize) throws GlobalException {

        Example example = new Example(GuestBook.class);
        example.createCriteria().andEqualTo("delete", 0);
        example.orderBy("createTime").desc();

        PageHelper.startPage(pageNum, pageSize);
        List<GuestBook> guestBookList = this.getBaseMapper().selectByExample(example);
        if (CollectionUtils.isEmpty(guestBookList)) {
            return new ArrayList<>();
        }

        List<Integer> pidList = guestBookList.stream().map(GuestBook::getId).collect(Collectors.toList());
        Example parentExample = Example.builder(GuestBook.class).where(Sqls.custom().andIn("id", pidList)).build();
        List<GuestBook> parentList = this.getBaseMapper().selectByExample(parentExample);
        Map<Integer, GuestBook> parentMap = parentList.stream().collect(Collectors.toMap(GuestBook::getId, Function.identity(), (k1, k2)->k1));

        for (GuestBook guestBook : guestBookList) {
            guestBook.setIpInfo(IpUtil.getProvinceAndCity(guestBook.getIpAddress()));
            guestBook.setIpAddress("");
            // 父级评论
            GuestBook parent = parentMap.get(guestBook.getPId());
            if (parent != null) {
                parent.setIpAddress("");
                guestBook.setParent(parent);
            }
            guestBook.setTimeDesc(DateUtil.timeDesc(guestBook.getCreateTime()));
        }

        return guestBookList;
    }

    @Override
    public List<GuestBook> getGuestBookListByIndex(Integer pageNum, Integer pageSize) throws GlobalException {

        Example example = new Example(GuestBook.class);
        example.createCriteria().andEqualTo("delete", 0)
                                .andEqualTo("bannerId", 0);
        example.orderBy("createTime").desc();

        PageHelper.startPage(pageNum, pageSize);
        List<GuestBook> parentList = this.getBaseMapper().selectByExample(example);
        if (CollectionUtils.isEmpty(parentList)) {
            return new ArrayList<>();
        }

        // 查询子级回复列表
        List<Integer> pidList = parentList.stream().map(GuestBook::getId).collect(Collectors.toList());
        Example replyExample = Example.builder(GuestBook.class).where(Sqls.custom().andIn("bannerId", pidList)).build();
        List<GuestBook> replyList = this.getBaseMapper().selectByExample(replyExample);
        Map<Integer, List<GuestBook>> replyMap = replyList.stream().collect(Collectors.groupingBy(GuestBook::getBannerId));

        for (GuestBook guestBook : parentList) {
            // 判断是否为博主
            guestBook.setIpInfo(IpUtil.getProvinceAndCity(guestBook.getIpAddress()));
            guestBook.setIpAddress("");
            // 子级评论
            List<GuestBook> children = replyMap.get(guestBook.getId());
            if (!CollectionUtils.isEmpty(children)) {
                children.forEach(i -> {
                    i.setIpInfo(IpUtil.getProvinceAndCity(i.getIpAddress()));
                    i.setIpAddress("");
                    i.setTimeDesc(DateUtil.timeDesc(i.getCreateTime()));
                });
                guestBook.setReplyList(children);
            }
            guestBook.setTimeDesc(DateUtil.timeDesc(guestBook.getCreateTime()));
        }

        return parentList;
    }

    @Override
    public void saveGuestBookByIndex(GuestBook guestBook) throws GlobalException {

        String toEmail = "", subject = "";
        User guestBookUser = this.userService.checkUser(guestBook.getNickname(), guestBook.getEmail(), guestBook.getAvatar());

        GuestBook parent = super.findById(guestBook.getPId());
        if (parent != null && !parent.getDelete()) {
            guestBook.setBannerId(parent.getBannerId() > 0 ? parent.getBannerId() : parent.getId()).setSourceNickname(parent.getNickname());

            User user = this.userService.findById(parent.getUserId());
            if (user != null) {
                toEmail = user.getEmail();
                subject = guestBookUser.getNickname() + "回复你的留言";
            }
        } else{
            toEmail = this.configService.getConfigValue(ConfigEnum.EMAIL.getName());
            subject = guestBookUser.getNickname() + "给你留言";
        }

        guestBook.setUserId(guestBookUser.getId()).setNickname(guestBookUser.getNickname()).setAvatar(guestBookUser.getAvatar());
        this.saveModel(guestBook);

        this.eventPublisher.emit(new MessageEvent(this, guestBook.getNickname() + "给你留言了", MessageEvent.Type.GUEST_BOOK));

        this.emailService.sendEmail(toEmail, subject, guestBook.getContent());
    }

}
