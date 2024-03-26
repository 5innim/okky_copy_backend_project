package com.innim.okkycopy.domain.board.repository;

import com.innim.okkycopy.domain.board.entity.Post;
import com.innim.okkycopy.domain.board.entity.PostExpression;
import com.innim.okkycopy.domain.member.entity.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostExpressionRepository extends JpaRepository<PostExpression, Long> {

    @Query("SELECT e FROM PostExpression e WHERE e.post = :post AND e.member = :member")
    Optional<PostExpression> findByMemberAndPost(@Param("post") Post post, @Param("member") Member member);
}
