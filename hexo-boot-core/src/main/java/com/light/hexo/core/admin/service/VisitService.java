package com.light.hexo.core.admin.service;

import com.light.hexo.common.base.BaseService;
import com.light.hexo.common.component.event.EventService;
import com.light.hexo.common.exception.GlobalException;
import com.light.hexo.mapper.model.Visit;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * @Author MoonlightL
 * @ClassName: VisitService
 * @ProjectName hexo-boot
 * @Description: 访问 Service
 * @DateTime 2020/9/16 13:44
 */
public interface VisitService extends BaseService<Visit>, EventService {

    /**
     * 批量查询访问记录
     * @param start
     * @param end
     * @return
     * @throws GlobalException
     */
    List<Map<String, Object>> listVisitByDates(LocalDate start, LocalDate end) throws GlobalException;

    /**
     * 今日访问人数
     * @return
     * @throws GlobalException
     */
    Integer getTodayVisitNum() throws GlobalException;

    Visit findFirstExpireRecord(LocalDateTime dateTime);

    List<Visit> findExpireList(Integer lastId, LocalDateTime dateTime);
}
