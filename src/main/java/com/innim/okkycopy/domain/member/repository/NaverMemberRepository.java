package com.innim.okkycopy.domain.member.repository;

import com.innim.okkycopy.domain.member.entity.NaverMember;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NaverMemberRepository extends JpaRepository<NaverMember, Long> {
    Optional<NaverMember> findByProviderId(String providerId);
}
