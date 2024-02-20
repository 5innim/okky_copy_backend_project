package com.innim.okkycopy.domain.board.repository;

import com.innim.okkycopy.domain.board.entity.Post;
import com.innim.okkycopy.domain.board.entity.PostHate;
import com.innim.okkycopy.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PostHateRepository extends JpaRepository<PostHate, Long> {
    @Query("SELECT h FROM PostHate h WHERE h.post = :post AND h.member = :member")
    Optional<PostHate> findByMemberAndPost(@Param("post") Post post, @Param("member") Member member);
}
