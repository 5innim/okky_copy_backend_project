package com.innim.okkycopy.domain.member.repository;

import com.innim.okkycopy.domain.member.entity.KakaoMember;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KakaoMemberRepository extends JpaRepository<KakaoMember, Long> {

    Optional<KakaoMember> findByProviderId(String providerId);

    boolean existsByEmail(String email);
}
