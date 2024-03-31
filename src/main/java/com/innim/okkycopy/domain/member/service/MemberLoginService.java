package com.innim.okkycopy.domain.member.service;

import com.innim.okkycopy.domain.member.MemberRepository;
import com.innim.okkycopy.domain.member.entity.Member;
import com.innim.okkycopy.global.error.ErrorCase;
import com.innim.okkycopy.global.error.exception.StatusCode401Exception;
import com.innim.okkycopy.global.error.exception.StatusCodeException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberLoginService {

    private final MemberRepository memberRepository;

    @Transactional
    public void modifyMemberLoginDate(long memberId, LocalDateTime loginDate)
        throws StatusCodeException {
        Optional<Member> optionalMember = memberRepository.findByMemberId(memberId);
        Member member = optionalMember.orElseThrow(
            () -> new StatusCode401Exception(ErrorCase._401_NO_SUCH_MEMBER));
        member.setLoginDate(loginDate);
    }

    @Transactional(readOnly = true)
    public Date findMemberLoginDate(long memberId)
        throws StatusCodeException {
        Optional<Member> optionalMember = memberRepository.findByMemberId(memberId);
        Member member = optionalMember.orElseThrow(
            () -> new StatusCode401Exception(ErrorCase._401_NO_SUCH_MEMBER));
        return (Date) Timestamp.valueOf(member.getLoginDate());
    }
}
