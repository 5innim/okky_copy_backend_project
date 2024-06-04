package com.innim.okkycopy.unit.member.service;


import static org.assertj.core.api.Assertions.catchException;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.innim.okkycopy.domain.member.dto.request.OAuthMemberRequest;
import com.innim.okkycopy.domain.member.entity.GoogleMember;
import com.innim.okkycopy.domain.member.repository.GoogleMemberRepository;
import com.innim.okkycopy.domain.member.service.GoogleMemberService;
import com.innim.okkycopy.global.auth.CustomOAuth2User;
import com.innim.okkycopy.global.error.ErrorCase;
import com.innim.okkycopy.global.error.exception.StatusCode400Exception;
import com.innim.okkycopy.global.error.exception.StatusCode401Exception;
import com.innim.okkycopy.global.util.email.MailUtil;
import java.util.Collections;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;

@ExtendWith(MockitoExtension.class)
public class GoogleMemberServiceTest {

    @Mock
    GoogleMemberRepository googleMemberRepository;
    @Mock
    MailUtil mailUtil;
    @InjectMocks
    GoogleMemberService googleMemberService;

    @Nested
    class _addGoogleMember_$OAuthMemberRequest_$String_$HttpServletRequest {

        @Test
        void given_noExistSession_then_throwErrorCase401006() {
            // given
            MockHttpServletRequest httpServletRequest = httpServletRequest();
            String provider = "google";
            OAuthMemberRequest oAuthMemberRequest = oAuthMemberRequest();

            // when
            Exception exception = catchException(() -> {
                googleMemberService.addGoogleMember(oAuthMemberRequest, provider, httpServletRequest);
            });

            // then
            assertThat(exception).isInstanceOf(StatusCode401Exception.class);
            assertThat(((StatusCode401Exception) exception).getErrorCase()).isEqualTo(ErrorCase._401_NOT_EXIST_SESSION);
        }

        @Test
        void given_noExistKey_then_throwErrorCase401009() {
            // given
            MockHttpServletRequest httpServletRequest = httpServletRequest();
            httpServletRequest.setSession(new MockHttpSession());
            String provider = "google";
            OAuthMemberRequest oAuthMemberRequest = oAuthMemberRequest();

            // when
            Exception exception = catchException(() -> {
                googleMemberService.addGoogleMember(oAuthMemberRequest, provider, httpServletRequest);
            });

            // then
            assertThat(exception).isInstanceOf(StatusCode401Exception.class);
            assertThat(((StatusCode401Exception) exception).getErrorCase()).isEqualTo(ErrorCase._401_NO_SUCH_KEY);
        }

        @Test
        void given_notMatchedProvider_then_throwErrorCase400024() {
            // given
            MockHttpServletRequest httpServletRequest = httpServletRequest();
            httpServletRequest.setSession(new MockHttpSession());
            CustomOAuth2User customOAuth2User = customOAuth2User();
            httpServletRequest.getSession().setAttribute("testKey", customOAuth2User);
            String provider = "not_google";
            OAuthMemberRequest oAuthMemberRequest = oAuthMemberRequest();

            // when
            Exception exception = catchException(() -> {
                googleMemberService.addGoogleMember(oAuthMemberRequest, provider, httpServletRequest);
            });

            // then
            assertThat(exception).isInstanceOf(StatusCode400Exception.class);
            assertThat(((StatusCode400Exception) exception).getErrorCase()).isEqualTo(
                ErrorCase._400_NO_ACCEPTABLE_PARAMETER);
        }

        @Test
        void given_correctRequest_then_saveAndSendEmail() {
            // given
            MockHttpServletRequest httpServletRequest = httpServletRequest();
            httpServletRequest.setSession(new MockHttpSession());
            CustomOAuth2User customOAuth2User = customOAuth2User();
            httpServletRequest.getSession().setAttribute("testKey", customOAuth2User);
            String provider = "google";
            OAuthMemberRequest oAuthMemberRequest = oAuthMemberRequest();
            GoogleMember googleMember = googleMember();
            given(googleMemberRepository.save(any(GoogleMember.class))).willReturn(googleMember);

            // when
            Long memberId = googleMemberService.addGoogleMember(oAuthMemberRequest, provider, httpServletRequest);

            // then
            then(googleMemberRepository).should(times(1)).save(any(GoogleMember.class));
            then(googleMemberRepository).shouldHaveNoMoreInteractions();
            then(mailUtil).should(times(1))
                .sendAuthenticateEmailAndPutCache(any(String.class), any(Long.class), any(String.class));
            then(mailUtil).shouldHaveNoMoreInteractions();
            assertThat(memberId).isEqualTo(1L);
        }


        MockHttpServletRequest httpServletRequest() {
            return new MockHttpServletRequest();
        }

        OAuthMemberRequest oAuthMemberRequest() {
            return OAuthMemberRequest.builder()
                .key("testKey")
                .nickname("testNickname")
                .profile("testProfile")
                .emailCheck(true)
                .build();
        }

        CustomOAuth2User customOAuth2User() {
            return new CustomOAuth2User(null, Collections.singletonMap("key", "value"), "key", null, "google");
        }

        GoogleMember googleMember() {
            return GoogleMember.builder()
                .memberId(1L)
                .name("testName")
                .email("test@test.com")
                .build();
        }


    }

}
