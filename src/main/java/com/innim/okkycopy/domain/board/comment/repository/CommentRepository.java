package com.innim.okkycopy.domain.board.comment.repository;

import com.innim.okkycopy.domain.board.comment.entity.Comment;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    Optional<Comment> findByCommentId(long commentId);

    List<Comment> findByParentIdOrderByCreatedDateAsc(long parentId);

    @Query(value =
        "SELECT COALESCE(m.nickname, CASE WHEN sc.mentioned_member IS NOT NULL THEN 'un-known' ELSE NULL END) "
            + "FROM (SELECT c.comment_id, c.mentioned_member, c.created_date FROM comment c WHERE c.comment_id IN :commentIds) sc "
            + "LEFT JOIN member m "
            + "ON sc.mentioned_member = m.member_id "
            + "ORDER BY sc.created_date ASC", nativeQuery = true)
    List<String> findMentionedNickname(@Param("commentIds") List<Long> commentIds);

}
