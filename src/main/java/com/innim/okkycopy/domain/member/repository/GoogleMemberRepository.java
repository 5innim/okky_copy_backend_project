package com.innim.okkycopy.domain.member.repository;

import com.innim.okkycopy.domain.member.entity.GoogleMember;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;


public interface GoogleMemberRepository extends JpaRepository<GoogleMember, Long> {

    Optional<GoogleMember> findByProviderId(String providerId);

    boolean existsByEmail(String email);
}
