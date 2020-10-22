package com.light.hexo.common.base;

import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

/**
 * @Author MoonlightL
 * @ClassName: BaseMapper
 * @ProjectName hexo-boot
 * @Description: Mapper 基类
 * @DateTime 2020/7/29 14:55
 */
public interface BaseMapper <T> extends Mapper<T>, MySqlMapper<T> {
}
