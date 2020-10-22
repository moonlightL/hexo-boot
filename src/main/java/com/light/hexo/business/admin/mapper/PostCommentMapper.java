package com.light.hexo.business.admin.mapper;

import com.light.hexo.business.admin.model.PostComment;
import com.light.hexo.common.base.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author MoonlightL
 * @ClassName: PostCommentMapper
 * @ProjectName hexo-boot
 * @Description: 文章评论 Mapper
 * @DateTime 2020/8/21 11:50
 */
public interface PostCommentMapper extends BaseMapper<PostComment> {

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
    List<PostComment> selectListByBannerIdList(@Param("bannerIdList") List<Integer> bannerIdList);

}
