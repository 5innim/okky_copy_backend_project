package com.innim.okkycopy.domain.member.repository;

import com.innim.okkycopy.domain.member.entity.OkkyMember;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OkkyMemberRepository extends JpaRepository<OkkyMember, Long> {
    boolean existsById(String id);

    boolean existsByEmail(String email);

    Optional<OkkyMember> findById(String id);

}
