package com.innim.okkycopy.domain.board.knowledge.repository;

import com.innim.okkycopy.domain.board.entity.BoardTopic;
import com.innim.okkycopy.domain.board.knowledge.entity.KnowledgePost;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface KnowledgePostRepository extends JpaRepository<KnowledgePost, Long> {

    Optional<KnowledgePost> findByPostId(long postId);

    @Query("SELECT k FROM KnowledgePost k WHERE k.boardTopic = :boardTopic AND k.title LIKE CONCAT('%', :keyword, '%')")
    Page<KnowledgePost> findByTopicId(@Param("boardTopic") BoardTopic boardTopic, String keyword, Pageable pageable);

    @Query("SELECT k FROM KnowledgePost k WHERE k.title LIKE CONCAT('%', :keyword, '%')")
    Page<KnowledgePost> findAll(String keyword, Pageable pageable);
}
