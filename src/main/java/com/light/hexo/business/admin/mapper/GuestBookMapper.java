package com.light.hexo.business.admin.mapper;

import com.light.hexo.business.admin.model.GuestBook;
import com.light.hexo.common.base.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author MoonlightL
 * @ClassName: GuestBookMapper
 * @ProjectName hexo-boot
 * @Description: 留言 Mapper
 * @DateTime 2020/7/30 9:42
 */
public interface GuestBookMapper extends BaseMapper<GuestBook> {

    /**
     * 批量修改删除状态
     * @param idList
     */
    void updateDelStatusBatch(@Param("idList") List<Integer> idList);

    /**
     * 获取二级留言列表
     * @param bannerIdList
     * @return
     */
    List<GuestBook> selectListByBannerIdList(@Param("bannerIdList") List<Integer> bannerIdList);
}
