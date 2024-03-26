package com.innim.okkycopy.global.auth.filter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

import com.google.gson.Gson;
import com.innim.okkycopy.global.auth.AuthService;
import com.innim.okkycopy.global.auth.dto.request.LoginRequest;
import java.io.IOException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

@ExtendWith(MockitoExtension.class)
class IdPasswordAuthenticationFilterTest {

    @Mock
    AuthenticationManager authenticationManager;
    @Mock
    AuthService authService;
    @InjectMocks
    IdPasswordAuthenticationFilter filter;

    @Nested
    class AttemptAuthenticationTest {

        @Test
        public void given_notPostRequest_then_throwAuthenticationServiceException()
            throws IOException {
            // given
            MockHttpServletRequest request = new MockHttpServletRequest();
            MockHttpServletResponse response = new MockHttpServletResponse();
            request.setMethod("GET");

            // when
            Throwable thrown = catchThrowable(() -> {
                filter.attemptAuthentication(request, response);
            });

            // then
            assertThat(thrown).hasMessageContaining("Authentication method not supported: ");
        }

        @Test
        public void given_postRequest_then_callAuthenticateMethod() throws IOException {
            // given
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.setMethod("POST");
            request.setContentType("application/json");
            request.setContent(new Gson().toJson(loginRequest()).getBytes());

            MockHttpServletResponse response = new MockHttpServletResponse();

            given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .willReturn(mock(Authentication.class));

            // when
            Authentication authentication = filter.attemptAuthentication(request, response);

            // then
            then(authenticationManager)
                .should(times(1))
                .authenticate(any(UsernamePasswordAuthenticationToken.class));
            assertThat(authentication).isInstanceOf(Authentication.class);
        }
    }

    public LoginRequest loginRequest() {
        return new LoginRequest("test_id", "test_passwd");
    }


}