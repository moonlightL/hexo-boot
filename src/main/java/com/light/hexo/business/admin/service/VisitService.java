package com.light.hexo.business.admin.service;

import com.aliyun.oss.ServiceException;
import com.light.hexo.business.admin.model.Visit;
import com.light.hexo.common.base.BaseService;
import com.light.hexo.common.component.event.EventService;
import com.light.hexo.common.exception.GlobalException;

import java.time.LocalDate;
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
     * @throws ServiceException
     */
    List<Map<String, Object>> listVisitByDates(LocalDate start, LocalDate end) throws GlobalException;

    /**
     * 今日访问人数
     * @return
     * @throws GlobalException
     */
    Integer getTodayVisitNum() throws GlobalException;
}
