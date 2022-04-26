package com.light.hexo.plugin.statistic.service;

import com.light.hexo.common.component.event.BaseEvent;
import com.light.hexo.common.component.event.EventService;
import com.light.hexo.common.component.event.EventServiceFactory;
import com.light.hexo.common.util.DateUtil;
import com.light.hexo.plugin.statistic.event.StatisticConstant;
import com.light.hexo.plugin.statistic.event.StatisticEvent;
import com.light.hexo.plugin.statistic.mapper.VisitInfoMapper;
import com.light.hexo.plugin.statistic.model.VisitInfo;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * @Author MoonlightL
 * @ClassName: VisitInfoServiceImpl
 * @ProjectName hexo-boot
 * @Description: 访问记录信息 Service 实现
 * @DateTime 2022/4/24, 0024 16:59
 */
@Service
public class VisitInfoServiceImpl implements VisitInfoService, EventService, InitializingBean, DisposableBean {

    @Autowired
    private VisitInfoMapper visitInfoMapper;

    @Autowired
    private VisitDetailService visitDetailService;

    @Autowired
    private EventServiceFactory eventServiceFactory;

    @Override
    public String getEventType() {
        return StatisticConstant.EVENT_TYPE;
    }

    @Override
    public VisitInfo getVisitInfoByToday() {
        String period = DateUtil.ldToStr(LocalDate.now(), DateTimeFormatter.ofPattern("yyyyMMdd"));
        Example example = new Example(VisitInfo.class);
        example.createCriteria().andEqualTo("period", period);
        VisitInfo visitInfo = this.visitInfoMapper.selectOneByExample(example);
        return visitInfo;
    }

    @Override
    public VisitInfo getVisitInfoTotal() {
        return this.visitInfoMapper.sumVisitInfo();
    }

    @Override
    public void dealWithEvent(BaseEvent event) {
        StatisticEvent statisticEvent = (StatisticEvent) event;

        int pv = 1;
        int uv = 0;

        String ip = statisticEvent.getIp();
        String url = statisticEvent.getUrl();
        String browser = statisticEvent.getBrowser();

        LocalDate date = LocalDate.now();
        int detailCount = this.visitDetailService.checkVisitDetail(
                ip, LocalDateTime.of(date, LocalTime.MIN), LocalDateTime.of(date, LocalTime.MAX));
        if (detailCount == 0) {
            uv += 1;
        }

        // 详情
        this.visitDetailService.saveVisitDetail(ip, url, browser);

        // 信息
        String period = DateUtil.ldToStr(date, DateTimeFormatter.ofPattern("yyyyMMdd"));
        Example infoExample = new Example(VisitInfo.class);
        Example.Criteria infoCriteria = infoExample.createCriteria();
        infoCriteria.andEqualTo("period", period);
        VisitInfo visitInfo = this.visitInfoMapper.selectOneByExample(infoExample);
        if (visitInfo == null) {
            visitInfo = new VisitInfo();
            visitInfo.setPv(pv).setUv(uv).setPeriod(Integer.valueOf(period));
            this.visitInfoMapper.insert(visitInfo);
        } else {
            visitInfo.setPv(visitInfo.getPv() + pv).setUv(visitInfo.getUv() + uv);
            this.visitInfoMapper.updateByPrimaryKey(visitInfo);
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.eventServiceFactory.addEventService(this);
    }

    @Override
    public void destroy() throws Exception {
        this.eventServiceFactory.removeEventService(this);
    }
}
