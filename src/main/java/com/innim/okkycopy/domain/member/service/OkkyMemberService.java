package com.innim.okkycopy.domain.member.service;

import com.innim.okkycopy.domain.member.entity.OkkyMember;
import com.innim.okkycopy.domain.member.dto.request.MemberRequest;
import com.innim.okkycopy.domain.member.dto.response.MemberBriefResponse;
import com.innim.okkycopy.domain.member.repository.OkkyMemberRepository;
import com.innim.okkycopy.global.error.ErrorCase;
import com.innim.okkycopy.global.error.exception.StatusCode409Exception;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OkkyMemberService {

    private final PasswordEncoder passwordEncoder;
    private final OkkyMemberRepository okkyMemberRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.SERIALIZABLE)
    public MemberBriefResponse addMember(MemberRequest memberRequest) {

        if (okkyMemberRepository.existsById(memberRequest.getId())) {
            throw new StatusCode409Exception(ErrorCase._409_DUPLICATE_ID);
        }

        if (okkyMemberRepository.existsByEmail(memberRequest.getEmail())) {
            throw new StatusCode409Exception(ErrorCase._409_DUPLICATE_EMAIL);
        }

        memberRequest.encodePassword(passwordEncoder);

        OkkyMember member = OkkyMember.from(memberRequest);
        okkyMemberRepository.save(member);

        return MemberBriefResponse.from(member);
    }

}
