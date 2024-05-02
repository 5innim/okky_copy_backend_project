package com.innim.okkycopy.domain.member.service;

import com.innim.okkycopy.domain.member.entity.KakaoMember;
import com.innim.okkycopy.domain.member.repository.KakaoMemberRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class KakaoMemberService {

    private final KakaoMemberRepository kakaoMemberRepository;

    @Transactional(readOnly = true)
    public KakaoMember findKakaoMember(String email) {
        Optional<KakaoMember> kakaoMember = kakaoMemberRepository.findByEmail(email);
        return kakaoMember.orElse(null);
    }

}
