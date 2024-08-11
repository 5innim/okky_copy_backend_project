package com.innim.okkycopy.domain.board.event;

import com.innim.okkycopy.domain.board.entity.BoardTopic;
import com.innim.okkycopy.domain.board.event.entity.EventPost;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EventPostRepository extends JpaRepository<EventPost, Long> {

    Optional<EventPost> findByPostId(long postId);

    @Query("SELECT e FROM EventPost e WHERE e.boardTopic = :boardTopic AND e.title LIKE CONCAT('%', :keyword, '%')")
    @EntityGraph(attributePaths = {"member"})
    Page<EventPost> findPageByBoardTopicAndKeyword(@Param("boardTopic") BoardTopic boardTopic, String keyword,
        Pageable pageable);

    @Query("SELECT e FROM EventPost e WHERE e.title LIKE CONCAT('%', :keyword, '%')")
    @EntityGraph(attributePaths = {"member"})
    Page<EventPost> findPageByKeyword(String keyword, Pageable pageable);

}
