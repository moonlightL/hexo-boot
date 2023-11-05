package com.light.hexo.mapper.mapper;

import com.light.hexo.mapper.model.Visit;
import com.light.hexo.mapper.base.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * @Author MoonlightL
 * @ClassName: VisitMapper
 * @ProjectName hexo-boot
 * @Description: 访问 Mapper
 * @DateTime 2020/9/16 13:44
 */
public interface VisitMapper extends BaseMapper<Visit> {

    /**
     * 批量查询指定日期的访问数
     * @param start
     * @param end
     * @return
     */
    List<Map<String, Object>> selectVisitNumByDateList(@Param("start") LocalDate start, @Param("end") LocalDate end);

    Visit selectFirstExpireRecord(@Param("dateTime") LocalDateTime dateTime);

    List<Visit> selectExpireRecords(@Param("lastId") Integer lastId, @Param("dateTime") LocalDateTime dateTime);
}
