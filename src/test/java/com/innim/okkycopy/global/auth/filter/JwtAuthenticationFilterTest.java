package com.innim.okkycopy.global.auth.filter;

import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

import com.innim.okkycopy.global.auth.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import java.io.IOException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    CustomUserDetailsService customUserDetailsService;
    @InjectMocks
    JwtAuthenticationFilter filter;

    @Nested
    class DoFilterInternalTest {

        @Test
        public void given_noAuthorizationHeader_then_noAuthenticate()
            throws ServletException, IOException {
            // given
            FilterChain filterChain = mock(FilterChain.class);
            MockHttpServletRequest request = new MockHttpServletRequest();
            MockHttpServletResponse response = new MockHttpServletResponse();

            // when
            filter.doFilterInternal(request, response, filterChain);

            // then
            then(customUserDetailsService).shouldHaveNoInteractions();
            then(filterChain).should(times(1)).doFilter(request, response);
        }
    }
}