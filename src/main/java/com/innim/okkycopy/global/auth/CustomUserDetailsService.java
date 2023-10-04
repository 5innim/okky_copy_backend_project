package com.innim.okkycopy.global.auth;

import com.innim.okkycopy.domain.member.MemberRepository;
import com.innim.okkycopy.domain.member.entity.Member;
import java.util.List;
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

        return member.isEmpty() ? null : CustomUserDetails.builder()
            .authorities(List.of(() -> member.get().getRole()))
            .username(member.get().getId())
            .password(member.get().getPassword())
            .accountNonLocked(true)
            .accountNonExpired(true)
            .credentialsNonExpired(true)
            .enabled(true)
            .build();
    }
}
