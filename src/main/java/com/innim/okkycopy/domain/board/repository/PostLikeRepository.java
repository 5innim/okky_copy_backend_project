package com.innim.okkycopy.domain.board.repository;

import com.innim.okkycopy.domain.board.entity.Post;
import com.innim.okkycopy.domain.board.entity.PostLike;
import com.innim.okkycopy.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    @Query("SELECT l FROM PostLike l WHERE l.post = :post AND l.member = :member")
    Optional<PostLike> findByMemberAndPost(@Param("post") Post post, @Param("member") Member member);
}
