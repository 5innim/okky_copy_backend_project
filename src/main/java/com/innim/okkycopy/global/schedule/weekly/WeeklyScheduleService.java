package com.innim.okkycopy.global.schedule.weekly;

import com.innim.okkycopy.domain.board.entity.TagWeeklyStats;
import com.innim.okkycopy.domain.board.repository.TagWeeklyStatsRepository;
import com.innim.okkycopy.domain.board.service.TopTagService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WeeklyScheduleService {

    private final TopTagService topTagService;
    private final TagWeeklyStatsRepository tagWeeklyStatsRepository;

    @Transactional
    public void extractWeeklyTopTags() {
        topTagService.removeAllTopTag();
        List<TagWeeklyStats> results = tagWeeklyStatsRepository.findTop5ByOrderByCreatesDesc();
        int rank = 1;
        for (TagWeeklyStats result : results) {
            topTagService.addTopTag(rank, result.getName(), result.getCreates());
            rank++;
        }

        tagWeeklyStatsRepository.deleteAll();
    }

}
