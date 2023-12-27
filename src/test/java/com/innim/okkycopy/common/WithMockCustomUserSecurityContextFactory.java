package com.innim.okkycopy.common;

import com.innim.okkycopy.common.annotation.WithMockCustomUser;
import com.innim.okkycopy.domain.member.entity.Member;
import com.innim.okkycopy.global.auth.CustomUserDetails;
import com.innim.okkycopy.global.auth.enums.Role;
import java.time.LocalDateTime;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

public class WithMockCustomUserSecurityContextFactory implements
    WithSecurityContextFactory<WithMockCustomUser> {

    @Override
    public SecurityContext createSecurityContext(WithMockCustomUser annotation) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        CustomUserDetails principal = customUserDetailsMock();

        Authentication auth =
            UsernamePasswordAuthenticationToken.authenticated(principal, principal.getPassword(), principal.getAuthorities());
        context.setAuthentication(auth);

        return context;
    }

    public static CustomUserDetails customUserDetailsMock() {
        Member testMember = Member.builder()
            .memberId(1L)
            .id("test_id")
            .email("test@test.com")
            .createdDate(LocalDateTime.now())
            .role(Role.USER)
            .profile(null)
            .nickname("test_nickname")
            .emailCheck(true)
            .password("test_password")
            .name("test_name")
            .loginDate(null)
            .build();

        return new CustomUserDetails(testMember);
    }
}
