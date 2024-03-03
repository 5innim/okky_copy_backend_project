package com.innim.okkycopy.global.auth;

import com.innim.okkycopy.domain.member.MemberRepository;
import com.innim.okkycopy.domain.member.entity.Member;
import com.innim.okkycopy.global.error.exception.UserIdNotFoundException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public void updateMemberLoginDate(long memberId, LocalDateTime loginDate)
        throws UserIdNotFoundException {
        Optional<Member> optionalMember = memberRepository.findByMemberId(memberId);
        Member member = optionalMember.orElseThrow(
            () -> new UserIdNotFoundException("can not find user with \" + \"[\" + userId + \"]"));
        member.setLoginDate(loginDate);
    }

    @Transactional(readOnly = true)
    public Date selectMemberLoginDate(long memberId)
        throws UserIdNotFoundException {
        Optional<Member> optionalMember = memberRepository.findByMemberId(memberId);
        Member member = optionalMember.orElseThrow(
            () -> new UserIdNotFoundException("can not find user with \" + \"[\" + userId + \"]"));
        return (Date) Timestamp.valueOf(member.getLoginDate());
    }


}
