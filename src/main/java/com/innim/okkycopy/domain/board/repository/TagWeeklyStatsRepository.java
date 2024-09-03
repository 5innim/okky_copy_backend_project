package com.innim.okkycopy.domain.board.repository;

import com.innim.okkycopy.domain.board.entity.TagWeeklyStats;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface TagWeeklyStatsRepository extends JpaRepository<TagWeeklyStats, String> {

    @Modifying
    @Query("DELETE FROM TagWeeklyStats")
    void deleteAll();

    List<TagWeeklyStats> findTop5ByOrderByCreatesDesc();

}
