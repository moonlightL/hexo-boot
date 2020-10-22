package com.light.hexo.business.admin.service;

import com.light.hexo.business.admin.model.GuestBook;
import com.light.hexo.common.base.BaseService;
import com.light.hexo.common.component.event.EventService;
import com.light.hexo.common.exception.GlobalException;

import java.time.LocalDate;
import java.util.List;

/**
 * @Author MoonlightL
 * @ClassName: GuestBookService
 * @ProjectName hexo-boot
 * @Description: 留言 Service
 * @DateTime 2020/9/4 14:55
 */
public interface GuestBookService extends BaseService<GuestBook> {

    /**
     * 保存留言
     * @param guestBook
     * @throws GlobalException
     */
    void saveGuestBook(GuestBook guestBook) throws GlobalException;

    /**
     * 批量删除留言
     * @param idStrList
     * @throws GlobalException
     */
    void removeGuestBookBatch(List<String> idStrList) throws GlobalException;

    /**
     * 博主回复留言
     * @param guestBook
     * @throws GlobalException
     */
    void replyByAdmin(GuestBook guestBook) throws GlobalException;

    /**
     * 加入黑名单
     * @param guestBook
     * @param ipAddr
     * @throws GlobalException
     */
    void addBlacklist(GuestBook guestBook, String ipAddr) throws GlobalException;

    /**
     * 查询留言数
     * @return
     * @throws GlobalException
     */
    int getGuestBookNum() throws GlobalException;

    /**
     * 查询指定日期的留言数
     * @param date
     * @return
     * @throws GlobalException
     */
    int getGuestBookNum(LocalDate date) throws GlobalException;

    // =========================== 以下为前端页面请求 ============================

    /**
     *  获取留言板留言列表
     * @param pageNum
     * @param pageSize
     * @return
     * @throws GlobalException
     */
    List<GuestBook> listGuestBookByIndex(Integer pageNum, Integer pageSize) throws GlobalException;

    /**
     * 保存留言
     * @param guestBook
     * @throws GlobalException
     */
    void saveGuestBookByIndex(GuestBook guestBook) throws GlobalException;

}
