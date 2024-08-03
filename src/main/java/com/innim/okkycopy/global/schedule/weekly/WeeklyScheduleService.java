package com.innim.okkycopy.global.schedule.weekly;

import com.innim.okkycopy.domain.board.dto.result.TagCntQueryResult;
import com.innim.okkycopy.domain.board.service.TagCrudService;
import com.innim.okkycopy.domain.board.service.TopTagService;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WeeklyScheduleService {

    private final TagCrudService tagCrudService;
    private final TopTagService topTagService;

    @Transactional
    public void extractWeeklyTopTags(int listLimit) {
        LocalDateTime endDate = LocalDateTime.now()
            .with(TemporalAdjusters.previous(DayOfWeek.SUNDAY))
            .withHour(0)
            .withMinute(0)
            .withSecond(0)
            .withNano(0);
        LocalDateTime startDate = endDate.minusDays(7);

        topTagService.removeAllTopTag();
        List<TagCntQueryResult> results = tagCrudService.findTopTagsByCreatedDateRange(startDate, endDate,
            listLimit);

        int rank = 1;
        for (TagCntQueryResult result : results) {
            topTagService.addTopTag(rank, result.getName(), result.getCnt());
            rank++;
        }

    }

}
