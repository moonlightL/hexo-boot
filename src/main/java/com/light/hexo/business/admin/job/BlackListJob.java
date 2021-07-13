package com.light.hexo.business.admin.job;

import com.light.hexo.business.admin.model.Blacklist;
import com.light.hexo.business.admin.service.BlacklistService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author MoonlightL
 * @ClassName: BlackListJob
 * @ProjectName hexo-boot
 * @Description: 黑名单任务
 * @DateTime 2021/7/13 14:50
 */
@Component
@Slf4j
public class BlackListJob {

    @Autowired
    private BlacklistService blacklistService;

    /**
     * 检测临时黑名单
     */
    @Scheduled(cron = "0 0/1 * * * ?")
    public void checkExpireBlackList() {
        LocalDateTime now = LocalDateTime.now();
        List<Blacklist> blackLists = this.blacklistService.listExpireBlacks(now);
        if (CollectionUtils.isEmpty(blackLists)) {
            return;
        }

        List<Integer> idList = blackLists.stream().map(Blacklist::getId).collect(Collectors.toList());
        this.blacklistService.removeBatch(idList);
    }
}
