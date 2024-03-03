package com.innim.okkycopy.domain.board.repository;

import com.innim.okkycopy.domain.board.entity.Post;
import com.innim.okkycopy.domain.board.entity.Scrap;
import com.innim.okkycopy.domain.member.entity.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ScrapRepository extends JpaRepository<Scrap, Long> {

    @Query("SELECT s FROM Scrap s WHERE s.post = :post AND s.member = :member")
    Optional<Scrap> findByMemberAndPost(@Param("post") Post post, @Param("member") Member member);
}
