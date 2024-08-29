package com.innim.okkycopy.global.schedule;

import com.innim.okkycopy.global.common.storage.S3Uploader;
import com.innim.okkycopy.global.common.storage.image_usage.ImageUsage;
import com.innim.okkycopy.global.common.storage.image_usage.ImageUsageService;
import com.innim.okkycopy.global.schedule.weekly.WeeklyScheduleService;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ScheduledTasks {

    private final ImageUsageService imageUsageService;
    private final S3Uploader s3Uploader;
    private final WeeklyScheduleService weeklyScheduleService;

    @Scheduled(cron = "0 35 0 * * *")
    public void removeUnusedImageFromS3() {
        log.info("schedule start: removeUnusedImageFromS3");
        Date date = new Date();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
        LocalDateTime midnightTime = localDateTime.withHour(0).withMinute(0).withSecond(0).withNano(0);

        List<ImageUsage> imageUsages = imageUsageService.findImageUsagesByIsInUseAndCreatedDate(false, midnightTime);
        for (ImageUsage imageUsage : imageUsages) {
            s3Uploader.deleteFileFromS3(imageUsage.getImageUsageId().toString());
        }

        imageUsageService.removeImageUsagesByIsInUseAndCreatedDate(false, midnightTime);
    }

    @Scheduled(cron = "0 0 2 * * 1")
    public void doWeeklyJob() {
        log.info("schedule start: doWeeklyJob");
        weeklyScheduleService.extractWeeklyTopTags();
    }
}
