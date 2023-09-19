package com.innim.okkycopy.domain.member;

import com.innim.okkycopy.domain.member.dto.request.SignupRequest;
import com.innim.okkycopy.domain.member.dto.response.BriefMemberInfo;
import com.innim.okkycopy.domain.member.entity.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;

    public BriefMemberInfo insertMember(SignupRequest signupRequest) {
        log.trace("insertMember");
        signupRequest.encodePassword(passwordEncoder);

        Member member = Member.builder()
            .id(signupRequest.getId())
            .password(signupRequest.getPassword())
            .email(signupRequest.getEmail())
            .name(signupRequest.getName())
            .nickname(signupRequest.getNickname())
            .emailCheck(signupRequest.isEmailCheck())
            .build();

        memberRepository.save(member);

        log.trace(signupRequest.getPassword());
        return BriefMemberInfo.builder()
            .email(signupRequest.getEmail())
            .nickname(signupRequest.getNickname())
            .name(signupRequest.getName())
            .build();
    }

}
