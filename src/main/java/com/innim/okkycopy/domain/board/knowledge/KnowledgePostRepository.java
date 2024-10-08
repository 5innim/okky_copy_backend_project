package com.innim.okkycopy.domain.board.knowledge;

import com.innim.okkycopy.domain.board.entity.BoardTopic;
import com.innim.okkycopy.domain.board.knowledge.entity.KnowledgePost;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface KnowledgePostRepository extends JpaRepository<KnowledgePost, Long> {

    Optional<KnowledgePost> findByPostId(long postId);

    @Query("SELECT k FROM KnowledgePost k WHERE k.boardTopic = :boardTopic AND k.title LIKE CONCAT('%', :keyword, '%')")
    @EntityGraph(attributePaths = {"member"})
    Page<KnowledgePost> findPageByBoardTopicAndKeyword(@Param("boardTopic") BoardTopic boardTopic, String keyword,
        Pageable pageable);

    @Query("SELECT k FROM KnowledgePost k WHERE k.title LIKE CONCAT('%', :keyword, '%')")
    @EntityGraph(attributePaths = {"member"})
    Page<KnowledgePost> findPageByKeyword(String keyword, Pageable pageable);
}
