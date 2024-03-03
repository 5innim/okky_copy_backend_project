package com.innim.okkycopy.domain.board.comment.repository;

import com.innim.okkycopy.domain.board.comment.entity.Comment;
import com.innim.okkycopy.domain.board.comment.entity.CommentExpression;
import com.innim.okkycopy.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CommentExpressionRepository extends JpaRepository<CommentExpression, Long> {
    @Query("SELECT e FROM CommentExpression e WHERE e.comment = :comment AND e.member = :member")
    Optional<CommentExpression> findByMemberAndComment(@Param("comment") Comment comment, @Param("member") Member member);
}
