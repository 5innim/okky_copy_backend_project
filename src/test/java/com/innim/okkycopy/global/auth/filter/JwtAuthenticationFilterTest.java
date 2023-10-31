package com.innim.okkycopy.global.auth.filter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

import com.innim.okkycopy.global.auth.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

class JwtAuthenticationFilterTest {


    @Test
    void given_noneAuthorizationHeader_then_passFilter() throws ServletException, IOException {

        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        CustomUserDetailsService userDetailsService = mock(CustomUserDetailsService.class);
        FilterChain filterChain = mock(FilterChain.class);
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(userDetailsService);

        // when
        filter.doFilterInternal(request, response, filterChain);

        // then
        then(filterChain).should(times(1)).doFilter(request, response);

    }

}