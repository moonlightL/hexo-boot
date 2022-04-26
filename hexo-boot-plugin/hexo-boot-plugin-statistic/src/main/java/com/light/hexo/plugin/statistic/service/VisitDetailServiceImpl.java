package com.light.hexo.plugin.statistic.service;

import com.light.hexo.common.util.IpUtil;
import com.light.hexo.plugin.statistic.mapper.VisitDetailMapper;
import com.light.hexo.plugin.statistic.model.VisitDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;
import java.time.LocalDateTime;

/**
 * @Author MoonlightL
 * @ClassName: VisitDetailServiceImpl
 * @ProjectName hexo-boot
 * @Description: 访问记录详情 Service 实现
 * @DateTime 2022/4/25, 0025 11:12
 */
@Service
public class VisitDetailServiceImpl implements VisitDetailService {

    @Autowired
    private VisitDetailMapper visitDetailMapper;

    @Override
    public int checkVisitDetail(String ip, LocalDateTime startTime, LocalDateTime endTime) {
        Example detailExample = new Example(VisitDetail.class);
        Example.Criteria detailCriteria = detailExample.createCriteria();
        detailCriteria.andEqualTo("ip")
                .andBetween("createTime", startTime, endTime);
        return this.visitDetailMapper.selectCountByExample(detailExample);
    }

    @Override
    public void saveVisitDetail(String ip, String url, String browser) {
        VisitDetail visitDetail = new VisitDetail();
        visitDetail.setIp(ip)
                   .setUrl(url)
                   .setBrowser(browser)
                   .setCountry(IpUtil.getCountry(ip))
                   .setProvince(IpUtil.getProvince(ip))
                   .setCity(IpUtil.getCity(ip));
        this.visitDetailMapper.insert(visitDetail);
    }
}
