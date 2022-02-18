package com.light.hexo.business.admin.mapper;

import com.light.hexo.business.admin.model.Comment;
import com.light.hexo.common.base.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author MoonlightL
 * @ClassName: CommentMapper
 * @ProjectName hexo-boot
 * @Description: 评论 Mapper
 * @DateTime 2022/1/27, 0027 13:41
 */
public interface CommentMapper extends BaseMapper<Comment> {

    /**
     * 修改删除状态
     * @param id
     */
    void updateDelStatus(@Param("id") Integer id);

    /**
     * 批量修改删除状态
     * @param idList
     */
    void updateDelStatusBatch(@Param("idList") List<Integer> idList);

    /**
     * 获取二级评论列表
     * @param bannerIdList
     * @return
     */
    List<Comment> selectListByBannerIdList(@Param("bannerIdList") List<Integer> bannerIdList);
}
