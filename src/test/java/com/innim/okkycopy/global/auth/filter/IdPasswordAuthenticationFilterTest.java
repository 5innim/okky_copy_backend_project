package com.innim.okkycopy.global.auth.filter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import com.innim.okkycopy.domain.member.MemberService;
import com.innim.okkycopy.global.auth.AuthService;
import com.innim.okkycopy.global.auth.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;

@SpringBootTest
class IdPasswordAuthenticationFilterTest {

    @Autowired
    MemberService memberService;
    @Autowired
    CustomUserDetailsService userDetailsService;


    @Test
    void given_unmatchedUri_then_passFilter() throws ServletException, IOException {
        // given
        AuthenticationManager manager = mock(AuthenticationManager.class);
        AuthService service = mock(AuthService.class);
        IdPasswordAuthenticationFilter filter = new IdPasswordAuthenticationFilter(manager,
            service);

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = mock(FilterChain.class);
        request.setRequestURI("/not_login");

        // when
        filter.doFilter(request, response, filterChain);

        // then
        then(filterChain).should(times(1)).doFilter(request, response);
    }

}