package com.innim.okkycopy.domain.member;

import com.innim.okkycopy.domain.member.dto.request.SignupRequest;
import com.innim.okkycopy.domain.member.dto.response.BriefMemberInfo;
import com.innim.okkycopy.domain.member.entity.Member;
import com.innim.okkycopy.global.error.ErrorCode;
import com.innim.okkycopy.global.error.exception.DuplicateException;
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
public class MemberService {

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.SERIALIZABLE)
    public BriefMemberInfo insertMember(SignupRequest signupRequest) {

        if (memberRepository.existsById(signupRequest.getId()))
            throw new DuplicateException(ErrorCode._409_DUPLICATE_ID);

        if (memberRepository.existsByEmail(signupRequest.getEmail()))
            throw new DuplicateException(ErrorCode._409_DUPLICATE_EMAIL);

        signupRequest.encodePassword(passwordEncoder);

        Member member = Member.toMemberEntity(signupRequest);
        memberRepository.save(member);

        return BriefMemberInfo.toDto(member);
    }

}
