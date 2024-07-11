package com.innim.okkycopy.domain.member.service;

import com.innim.okkycopy.domain.member.dto.request.ChangePasswordRequest;
import com.innim.okkycopy.domain.member.dto.request.MemberRequest;
import com.innim.okkycopy.domain.member.dto.response.MemberBriefResponse;
import com.innim.okkycopy.domain.member.entity.Member;
import com.innim.okkycopy.domain.member.entity.OkkyMember;
import com.innim.okkycopy.domain.member.repository.MemberRepository;
import com.innim.okkycopy.domain.member.repository.OkkyMemberRepository;
import com.innim.okkycopy.global.error.ErrorCase;
import com.innim.okkycopy.global.error.exception.StatusCode400Exception;
import com.innim.okkycopy.global.error.exception.StatusCode401Exception;
import com.innim.okkycopy.global.error.exception.StatusCode409Exception;
import com.innim.okkycopy.global.util.email.MailUtil;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
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
    private final MemberRepository memberRepository;
    private final MailUtil mailUtil;

    @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.SERIALIZABLE)
    public MemberBriefResponse addMember(MemberRequest memberRequest) {

        if (okkyMemberRepository.existsById(memberRequest.getId())) {
            throw new StatusCode409Exception(ErrorCase._409_DUPLICATE_ID);
        }

        if (okkyMemberRepository.existsByEmail(memberRequest.getEmail())) {
            throw new StatusCode409Exception(ErrorCase._409_DUPLICATE_EMAIL);
        }

        OkkyMember member = OkkyMember.of(memberRequest, passwordEncoder);
        okkyMemberRepository.save(member);

        mailUtil.sendAuthenticateEmailAndPutCache(member.findEmail(), member.getMemberId(), member.getName());

        return MemberBriefResponse.from(member);
    }

    @Transactional
    public void modifyMember(Member requester, ChangePasswordRequest changePasswordRequest) {
        Member member = memberRepository.findByMemberId(requester.getMemberId()).orElseThrow(
            () -> new StatusCode401Exception(ErrorCase._401_NO_SUCH_MEMBER)
        );

        if (!(member instanceof OkkyMember okkyMember)) {
            throw new StatusCode400Exception(ErrorCase._400_NOT_SUPPORTED_CASE);
        }

        if (!passwordEncoder.matches(changePasswordRequest.getOldPassword(), okkyMember.getPassword())) {
            throw new StatusCode401Exception(ErrorCase._401_NOT_CORRECT_PASSWORD);
        }

        if (changePasswordRequest.getOldPassword().equals(changePasswordRequest.getNewPassword())) {
            throw new StatusCode400Exception(ErrorCase._400_NO_CHANGE_PASSWORD);
        }
        okkyMember.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));

        Date loginDate = new Date();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(loginDate.toInstant(), ZoneId.systemDefault());
        okkyMember.setLogoutDate(localDateTime);
    }

}
