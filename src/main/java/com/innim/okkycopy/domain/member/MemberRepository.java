package com.innim.okkycopy.domain.member;

import com.innim.okkycopy.domain.member.entity.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    boolean existsById(String id);
    boolean existsByEmail(String email);
    Optional<Member> findById(String id);
    Optional<Member> findByMemberId(long memberId);

}
