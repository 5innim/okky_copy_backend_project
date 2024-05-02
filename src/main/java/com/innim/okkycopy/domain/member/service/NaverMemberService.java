package com.innim.okkycopy.domain.member.service;

import com.innim.okkycopy.domain.member.entity.NaverMember;
import com.innim.okkycopy.domain.member.repository.NaverMemberRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NaverMemberService {

    private final NaverMemberRepository naverMemberRepository;

    @Transactional(readOnly = true)
    public NaverMember findNaverMember(String email) {
        Optional<NaverMember> naverMember = naverMemberRepository.findByEmail(email);
        return naverMember.orElse(null);
    }

}
