package com.light.hexo.core.admin.job;

import com.light.hexo.common.exception.GlobalException;
import com.light.hexo.core.admin.service.VisitService;
import com.light.hexo.mapper.model.Visit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author MoonlightL
 * @ClassName: VisitJob
 * @ProjectName hexo-boot
 * @Description: 访问定时器
 * @DateTime 2023/11/2 17:23
 */
@Component
@Slf4j
public class VisitJob {

    @Autowired
    private VisitService visitService;

    private volatile boolean taskStatus = false;

    /**
     * 清除8天前的记录
     */
    @Scheduled(cron = "0 30 0 * * ?")
    public void clearRecord() {
        LocalDateTime dateTime = LocalDateTime.now().minusDays(8);
        Visit first = this.visitService.findFirstExpireRecord(dateTime);
        if (first == null) {
            log.info("======= clearRecord 没有过期记录 time:{} =========", LocalDateTime.now());
            return;
        }

        if (taskStatus) {
            log.info("======= clearRecord 上一周期任务正在执行中 time:{} =========", LocalDateTime.now());
            return;
        }

        taskStatus = true;

        Integer lastId = first.getId();

        List<Visit> recordList = this.visitService.findExpireList(lastId, dateTime);
        recordList.add(0, first);

        while (!recordList.isEmpty()) {

            log.info("======= clearRecord 任务正在执行中 lastId:{}, time:{} =========", recordList.get(0).getId(), LocalDateTime.now());

            try {
                for (Visit visit : recordList) {
                    if (visit.getId() > lastId) {
                        lastId = visit.getId();
                    }
                }

                List<Integer> idList = recordList.stream().map(i -> i.getId()).collect(Collectors.toList());
                this.visitService.removeBatch(idList);
            } catch (GlobalException e) {
                e.printStackTrace();
            }

            recordList = this.visitService.findExpireList(lastId, dateTime);
        }

        taskStatus = false;
        log.info("======= clearRecord 任务执行结束 time:{} =========", LocalDateTime.now());
    }
}
