package com.innim.okkycopy.domain.board.comment.repository;

import com.innim.okkycopy.domain.board.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Optional<Comment> findByCommentId(long commentId);
    List<Comment> findByParentId(long parentId);
}
