package com.innim.okkycopy.domain.board.repository;

import com.innim.okkycopy.domain.board.dto.result.TagCntQueryResult;
import com.innim.okkycopy.domain.board.entity.Tag;
import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TagRepository extends JpaRepository<Tag, Long> {

    @Query("SELECT new com.innim.okkycopy.domain.board.dto.result.TagCntQueryResult(t.name, COUNT(t)) " +
        "FROM Tag t " +
        "WHERE t.createdDate >= :startDate AND t.createdDate < :endDate " +
        "GROUP BY t.name " +
        "ORDER BY COUNT(t) DESC")
    List<TagCntQueryResult> findTopTagsByCreatedDateRange(LocalDateTime startDate, LocalDateTime endDate,
        Pageable pageable);
}
