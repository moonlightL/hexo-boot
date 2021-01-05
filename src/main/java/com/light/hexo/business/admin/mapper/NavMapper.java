package com.light.hexo.business.admin.mapper;

import com.light.hexo.business.admin.model.Nav;
import com.light.hexo.common.base.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author MoonlightL
 * @ClassName: NavMapper
 * @ProjectName hexo-boot
 * @Description: 导航 Mapper
 * @DateTime 2020/12/14 17:30
 */
public interface NavMapper extends BaseMapper<Nav> {

    /**
     * 通过 parentId 获取导航
     * @param parentId
     * @return
     */
    List<Nav> selectListByParentId(@Param("id") int parentId);
}
