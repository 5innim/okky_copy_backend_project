package com.innim.okkycopy.domain.board.comment.repository;

import com.innim.okkycopy.domain.board.comment.entity.Comment;
import com.innim.okkycopy.domain.board.comment.entity.CommentLike;
import com.innim.okkycopy.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {

    @Query("SELECT l FROM CommentLike l WHERE l.comment = :comment AND l.member = :member")
    Optional<CommentLike> findByMemberAndComment(@Param("comment") Comment comment, @Param("member") Member member);
}
