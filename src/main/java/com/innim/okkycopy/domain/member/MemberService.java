package com.innim.okkycopy.domain.member;

import com.innim.okkycopy.domain.member.dto.request.SignupRequest;
import com.innim.okkycopy.domain.member.dto.response.BriefMemberInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {

    private final PasswordEncoder passwordEncoder;

    public BriefMemberInfo insertMember(SignupRequest signupRequest) {
        log.trace("insertMember");
        signupRequest.encodePassword(passwordEncoder);

        log.trace(signupRequest.getPassword());
        return BriefMemberInfo.builder()
            .email(signupRequest.getEmail())
            .nickname(signupRequest.getNickname())
            .name(signupRequest.getName())
            .build();
    }

}
