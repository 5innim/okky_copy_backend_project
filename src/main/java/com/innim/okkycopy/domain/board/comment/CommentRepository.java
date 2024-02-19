package com.innim.okkycopy.domain.board.comment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Optional<Comment> findByCommentId(long commentId);
    List<Comment> findByParentId(long parentId);
}
