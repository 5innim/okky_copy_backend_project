package com.innim.okkycopy.global.schedule.weekly;

import com.innim.okkycopy.domain.board.dto.result.TagCntQueryResult;
import com.innim.okkycopy.domain.board.entity.TopTag;
import com.innim.okkycopy.domain.board.repository.TagRepository;
import com.innim.okkycopy.domain.board.repository.TopTagRepository;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WeeklyScheduleService {

    private final TagRepository tagRepository;
    private final TopTagRepository topTagRepository;

    @Transactional
    public void removeAllTopTag() {
        topTagRepository.deleteAll();
    }

    @Transactional(readOnly = true)
    public List<TagCntQueryResult> findTopTagsByCreatedDateRange(LocalDateTime startDate, LocalDateTime endDate,
        int limit) {
        return tagRepository.findTopTagsByCreatedDateRange(startDate, endDate, PageRequest.of(0, limit));
    }

    @Transactional
    public void addTopTag(int rank, String name, int creates) {
        TopTag topTag = new TopTag(rank, name, creates);
        topTagRepository.save(topTag);
    }

    @Transactional
    public void extractWeeklyTopTags(int listLimit) {
        LocalDateTime endDate = LocalDateTime.now()
            .with(TemporalAdjusters.previous(DayOfWeek.SUNDAY))
            .withHour(0)
            .withMinute(0)
            .withSecond(0)
            .withNano(0);
        LocalDateTime startDate = endDate.minusDays(7);

        removeAllTopTag();
        List<TagCntQueryResult> results = findTopTagsByCreatedDateRange(startDate, endDate,
            listLimit);

        int rank = 1;
        for (TagCntQueryResult result : results) {
            addTopTag(rank, result.getName(), result.getCnt());
            rank++;
        }

    }

}
