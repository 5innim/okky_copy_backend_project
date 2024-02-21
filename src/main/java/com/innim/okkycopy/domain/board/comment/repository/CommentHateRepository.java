package com.innim.okkycopy.domain.board.comment.repository;

import com.innim.okkycopy.domain.board.comment.entity.Comment;
import com.innim.okkycopy.domain.board.comment.entity.CommentHate;
import com.innim.okkycopy.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CommentHateRepository extends JpaRepository<CommentHate, Long> {

    @Query("SELECT h FROM CommentHate h WHERE h.comment = :comment AND h.member = :member")
    Optional<CommentHate> findByMemberAndComment(@Param("comment") Comment comment, @Param("member") Member member);
}
