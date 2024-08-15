package com.innim.okkycopy.domain.board.comment.repository;

import com.innim.okkycopy.domain.board.comment.entity.Comment;
import com.innim.okkycopy.domain.board.comment.entity.CommentExpression;
import com.innim.okkycopy.domain.member.entity.Member;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommentExpressionRepository extends JpaRepository<CommentExpression, Long> {

    @Query("SELECT e FROM CommentExpression e WHERE e.comment = :comment AND e.member = :member")
    Optional<CommentExpression> findByMemberAndComment(@Param("comment") Comment comment,
        @Param("member") Member member);

    @Query(value = "SELECT sce.expression_type "
        + "FROM (SELECT c.comment_id, c.created_date FROM comment c WHERE c.comment_id IN :commentIds) sc "
        + "LEFT JOIN (SELECT ce.comment_id, ce.member_id, ce.expression_type FROM comment_expression ce WHERE ce.member_id = :memberId) sce "
        + "ON sc.comment_id = sce.comment_id " + "ORDER BY sc.created_date ASC", nativeQuery = true)
    List<Short> findRequesterExpression(@Param("commentIds") List<Long> commentIds, @Param("memberId") Long memberId);

}
