package com.innim.okkycopy.global.auth;

import com.innim.okkycopy.domain.member.entity.OkkyMember;
import com.innim.okkycopy.domain.member.repository.MemberRepository;
import com.innim.okkycopy.domain.member.entity.Member;
import com.innim.okkycopy.domain.member.repository.OkkyMemberRepository;
import com.innim.okkycopy.global.error.ErrorCase;
import com.innim.okkycopy.global.error.exception.StatusCode401Exception;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;
    private final OkkyMemberRepository okkyMemberRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Optional<OkkyMember> member = okkyMemberRepository.findById(username);
        if (member.isEmpty()) {
            throw new UsernameNotFoundException("can not find user with " + "[" + username + "]");
        }

        return new CustomUserDetails(member.get());
    }

    @Transactional(readOnly = true)
    public UserDetails loadUserByUserId(Long userId) {
        Optional<Member> member = memberRepository.findByMemberId(userId);
        if (member.isEmpty()) {
            throw new StatusCode401Exception(ErrorCase._401_NO_SUCH_MEMBER);
        }

        return new CustomUserDetails(member.get());
    }

}
