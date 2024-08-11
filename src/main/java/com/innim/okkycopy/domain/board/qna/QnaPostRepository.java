package com.innim.okkycopy.domain.board.qna;

import com.innim.okkycopy.domain.board.entity.BoardTopic;
import com.innim.okkycopy.domain.board.qna.entity.QnaPost;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface QnaPostRepository extends JpaRepository<QnaPost, Long> {

    Optional<QnaPost> findByPostId(long postId);

    @Query("SELECT q FROM QnaPost q WHERE q.boardTopic = :boardTopic AND q.title LIKE CONCAT('%', :keyword, '%')")
    @EntityGraph(attributePaths = {"member"})
    Page<QnaPost> findPageByBoardTopicAndKeyword(@Param("boardTopic") BoardTopic boardTopic, String keyword,
        Pageable pageable);

    @Query("SELECT q FROM QnaPost q WHERE q.title LIKE CONCAT('%', :keyword, '%')")
    @EntityGraph(attributePaths = {"member"})
    Page<QnaPost> findPageByKeyword(String keyword, Pageable pageable);
}
