package com.light.hexo.mapper.mapper;

import com.light.hexo.mapper.model.Tag;
import com.light.hexo.mapper.base.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author MoonlightL
 * @ClassName: TagMapper
 * @ProjectName hexo-boot
 * @Description: 标签 Mapper
 * @DateTime 2020/7/29 17:59
 */
public interface TagMapper extends BaseMapper<Tag> {

    /**
     * 批量插入
     * @param list
     * @return
     */
    int insertBatch(@Param("list") List<Tag> list);

    /**
     * 批量查询标签
     * @param tagNames
     * @return
     */
    List<Tag> selectListByTags(@Param("tagNames") String[] tagNames);

    /**
     * 根据名称获取标签
     * @param name
     * @return
     */
    Tag selectOneByName(@Param("name") String name);
}
