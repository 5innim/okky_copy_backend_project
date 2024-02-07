package com.innim.okkycopy.domain.board.knowledge.repository;

import com.innim.okkycopy.domain.board.knowledge.entity.KnowledgeComment;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KnowledgeCommentRepository extends JpaRepository<KnowledgeComment, Long> {
    Optional<KnowledgeComment> findByCommentId(long commentId);
}
