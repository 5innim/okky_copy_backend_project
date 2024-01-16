package com.innim.okkycopy.global.auth.filter;

import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.assertj.core.api.Assertions.*;

import com.innim.okkycopy.global.auth.AuthService;
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
class JwtRefreshFilterTest {

    @Mock
    AuthService authService;
    @InjectMocks
    JwtRefreshFilter filter;

    @Nested
    class doFilterInternalTest {
        @Test
        void given_noAuthorizationHeader_then_noAuthenticate() throws ServletException, IOException {
            // given
            FilterChain filterChain = mock(FilterChain.class);
            MockHttpServletRequest request = new MockHttpServletRequest();
            MockHttpServletResponse response = new MockHttpServletResponse();
            request.setMethod("POST");
            request.setServletPath("/refresh");

            // when
            filter.doFilterInternal(request, response, filterChain);

            // then
            then(authService).shouldHaveNoInteractions();
            then(filterChain).should(times(1)).doFilter(request, response);
        }
    }

    @Nested
    class requiresRefreshTest {
        @Test
        void given_postAndRefreshUri_then_returnTrue() {
            // given
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.setMethod("POST");
            request.setServletPath("/refresh");

            // when
            boolean res = filter.requiresRefresh(request);

            // then
            assertThat(res).isTrue();
        }
    }
}