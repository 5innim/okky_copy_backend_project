package com.innim.okkycopy.domain.board.knowledge.repository;

import com.innim.okkycopy.domain.board.knowledge.entity.KnowledgePost;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KnowledgePostRepository extends JpaRepository<KnowledgePost, Long> {
    Optional<KnowledgePost> findByPostId(long postId);
}
