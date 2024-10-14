package com.innim.okkycopy.unit.member.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.innim.okkycopy.domain.member.dto.request.OAuthMemberRequest;
import com.innim.okkycopy.domain.member.entity.NaverMember;
import com.innim.okkycopy.domain.member.repository.NaverMemberRepository;
import com.innim.okkycopy.domain.member.service.NaverMemberService;
import com.innim.okkycopy.global.auth.CustomOAuth2User;
import com.innim.okkycopy.global.error.ErrorCase;
import com.innim.okkycopy.global.error.exception.StatusCode400Exception;
import com.innim.okkycopy.global.error.exception.StatusCode401Exception;
import com.innim.okkycopy.global.common.email.MailManager;
import com.innim.okkycopy.global.util.EncryptionUtil;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;

@ExtendWith(MockitoExtension.class)
public class NaverMemberServiceTest {

    @Mock
    NaverMemberRepository naverMemberRepository;
    @Mock
    MailManager mailManager;
    @InjectMocks
    NaverMemberService naverMemberService;

    @Nested
    class _addNaverMember_$OAuthMemberRequest_$String_$HttpServletRequest {

        @Test
        void given_noExistSession_then_throwErrorCase401006() throws NoSuchAlgorithmException {
            // given
            MockHttpServletRequest httpServletRequest = httpServletRequest();
            String provider = "naver";
            OAuthMemberRequest oAuthMemberRequest = oAuthMemberRequest();

            // when
            Exception exception = catchException(() -> {
                naverMemberService.addNaverMember(oAuthMemberRequest, provider, httpServletRequest);
            });

            // then
            assertThat(exception).isInstanceOf(StatusCode401Exception.class);
            assertThat(((StatusCode401Exception) exception).getErrorCase()).isEqualTo(ErrorCase._401_NOT_EXIST_SESSION);
        }

        @Test
        void given_noExistKey_then_throwErrorCase401009() throws NoSuchAlgorithmException {
            // given
            MockHttpServletRequest httpServletRequest = httpServletRequest();
            httpServletRequest.setSession(new MockHttpSession());
            String provider = "naver";
            OAuthMemberRequest oAuthMemberRequest = oAuthMemberRequest();

            // when
            Exception exception = catchException(() -> {
                naverMemberService.addNaverMember(oAuthMemberRequest, provider, httpServletRequest);
            });

            // then
            assertThat(exception).isInstanceOf(StatusCode401Exception.class);
            assertThat(((StatusCode401Exception) exception).getErrorCase()).isEqualTo(ErrorCase._401_NO_SUCH_KEY);
        }

        @Test
        void given_notMatchedProvider_then_throwErrorCase400024() throws NoSuchAlgorithmException {
            // given
            MockHttpServletRequest httpServletRequest = httpServletRequest();
            httpServletRequest.setSession(new MockHttpSession());
            CustomOAuth2User customOAuth2User = customOAuth2User();
            httpServletRequest.getSession().setAttribute("testKey", customOAuth2User);
            String provider = "not_naver";
            OAuthMemberRequest oAuthMemberRequest = oAuthMemberRequest();

            // when
            Exception exception = catchException(() -> {
                naverMemberService.addNaverMember(oAuthMemberRequest, provider, httpServletRequest);
            });

            // then
            assertThat(exception).isInstanceOf(StatusCode400Exception.class);
            assertThat(((StatusCode400Exception) exception).getErrorCase()).isEqualTo(
                ErrorCase._400_NO_ACCEPTABLE_PARAMETER);
        }

        @Test
        void given_invoke_then_saveAndSendEmail() throws NoSuchAlgorithmException {
            // given
            MockHttpServletRequest httpServletRequest = httpServletRequest();
            httpServletRequest.setSession(new MockHttpSession());
            CustomOAuth2User customOAuth2User = customOAuth2User();
            httpServletRequest.getSession().setAttribute("testKey", customOAuth2User);
            String provider = "naver";
            OAuthMemberRequest oAuthMemberRequest = oAuthMemberRequest();
            NaverMember naverMember = naverMember();
            given(naverMemberRepository.save(any(NaverMember.class))).willReturn(naverMember);

            // when
            Long memberId = naverMemberService.addNaverMember(oAuthMemberRequest, provider, httpServletRequest);

            // then
            then(naverMemberRepository).should(times(1)).save(any(NaverMember.class));
            then(naverMemberRepository).shouldHaveNoMoreInteractions();
            then(mailManager).should(times(1))
                .sendAuthenticateEmailAndPutCache(any(String.class), any(Long.class), any(String.class));
            then(mailManager).shouldHaveNoMoreInteractions();
            assertThat(memberId).isEqualTo(1L);
        }


        MockHttpServletRequest httpServletRequest() {
            return new MockHttpServletRequest();
        }

        OAuthMemberRequest oAuthMemberRequest() throws NoSuchAlgorithmException {
            OAuthMemberRequest request = new OAuthMemberRequest();
            request.setKey(EncryptionUtil.base64Encode("testKey"));
            request.setNickname("testNickname");
            request.setProfile("testProfile");
            request.setEmailCheck(true);
            return request;
        }

        CustomOAuth2User customOAuth2User() {
            HashMap<String, Object> map = new HashMap<>();
            map.put("key", "value");
            map.put("response", new LinkedHashMap<>());
            return new CustomOAuth2User(null, map, "key", null, "naver");
        }

        NaverMember naverMember() {
            return NaverMember.builder()
                .memberId(1L)
                .name("testName")
                .email("test@test.com")
                .build();
        }


    }

}
