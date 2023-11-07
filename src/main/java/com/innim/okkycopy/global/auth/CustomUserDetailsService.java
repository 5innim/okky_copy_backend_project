package com.innim.okkycopy.global.auth;

import com.innim.okkycopy.domain.member.MemberRepository;
import com.innim.okkycopy.domain.member.entity.Member;
import com.innim.okkycopy.global.error.exception.UserIdNotFoundException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Optional<Member> member = memberRepository.findById(username);
        if (member.isEmpty()) throw new UsernameNotFoundException("can not find user with " + "[" + username + "]");

        return new CustomUserDetails(member.get());
    }

    public UserDetails loadUserByUserId(Long userId) throws UserIdNotFoundException {
        Optional<Member> member = memberRepository.findByMemberId(userId);
        if (member.isEmpty()) throw new UserIdNotFoundException("can not find user with " + "[" + userId + "]");

        return new CustomUserDetails(member.get());
    }

}
