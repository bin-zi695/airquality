package com.example.airquality.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SyncScheduler {

    private static final Logger log = LoggerFactory.getLogger(SyncScheduler.class);
    private final DataSyncService dataSyncService;

    public SyncScheduler(DataSyncService dataSyncService) {
        this.dataSyncService = dataSyncService;
    }

    @Scheduled(cron = "0 0 1 * * ?")
    public void syncYesterday() {
        log.info("定时任务: 凌晨1:00 拉取昨日数据");
        dataSyncService.syncYesterdayData();
    }

    @Scheduled(cron = "0 0 8 * * ?")
    public void syncMorning() {
        log.info("定时任务: 早上8:00 拉取今日数据");
        dataSyncService.syncTodayData();
    }

    @Scheduled(cron = "0 0 14 * * ?")
    public void syncAfternoon() {
        log.info("定时任务: 下午14:00 更新今日数据");
        dataSyncService.syncTodayData();
    }

    @Scheduled(cron = "0 0 20 * * ?")
    public void syncEvening() {
        log.info("定时任务: 晚上20:00 更新今日数据");
        dataSyncService.syncTodayData();
    }
}
