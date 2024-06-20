package com.innim.okkycopy.unit.member;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.innim.okkycopy.common.WithMockCustomUserSecurityContextFactory;
import com.innim.okkycopy.domain.member.MemberController;
import com.innim.okkycopy.domain.member.dto.request.ChangePasswordRequest;
import com.innim.okkycopy.domain.member.dto.request.MemberRequest;
import com.innim.okkycopy.domain.member.dto.request.OAuthMemberRequest;
import com.innim.okkycopy.domain.member.dto.request.ProfileUpdateRequest;
import com.innim.okkycopy.domain.member.dto.request.UpdateEmailRequest;
import com.innim.okkycopy.domain.member.dto.response.MemberBriefResponse;
import com.innim.okkycopy.domain.member.dto.response.MemberDetailsResponse;
import com.innim.okkycopy.domain.member.entity.Member;
import com.innim.okkycopy.domain.member.service.GoogleMemberService;
import com.innim.okkycopy.domain.member.service.KakaoMemberService;
import com.innim.okkycopy.domain.member.service.MemberCrudService;
import com.innim.okkycopy.domain.member.service.MemberEmailService;
import com.innim.okkycopy.domain.member.service.NaverMemberService;
import com.innim.okkycopy.domain.member.service.OkkyMemberService;
import com.innim.okkycopy.global.auth.CustomUserDetails;
import com.innim.okkycopy.global.error.ErrorCase;
import com.innim.okkycopy.global.error.exception.StatusCode400Exception;
import com.innim.okkycopy.global.error.exception.StatusCode500Exception;
import com.innim.okkycopy.global.util.EncryptionUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

@ExtendWith(MockitoExtension.class)
public class MemberControllerTest {

    @Mock
    MemberCrudService memberCrudService;
    @Mock
    OkkyMemberService okkyMemberService;
    @Mock
    GoogleMemberService googleMemberService;
    @Mock
    KakaoMemberService kakaoMemberService;
    @Mock
    NaverMemberService naverMemberService;
    @Mock
    MemberEmailService memberEmailService;
    @InjectMocks
    MemberController memberController;

    @Nested
    class _memberAdd_$MemberRequest {

        @Test
        void given_request_then_responseMemberBriefResponse() {
            // given
            MemberRequest memberRequest = memberRequest();
            MemberBriefResponse memberBriefResponse = memberBriefResponse();
            given(okkyMemberService.addMember(any(MemberRequest.class))).willReturn(memberBriefResponse);

            // when
            ResponseEntity<MemberBriefResponse> response = memberController.memberAdd(memberRequest);

            // then
            then(okkyMemberService).should(times(1)).addMember(memberRequest);
            then(okkyMemberService).shouldHaveNoMoreInteractions();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertThat(response.getBody()).isEqualTo(memberBriefResponse);
        }

        MemberRequest memberRequest() {
            return MemberRequest.builder()
                .id("testId")
                .password("testPassword**")
                .email("testEmail@email.com")
                .emailCheck(true)
                .name("testName")
                .nickname("testNickname")
                .build();
        }

        MemberBriefResponse memberBriefResponse() {
            return MemberBriefResponse.builder()
                .email("testEmail@email.com")
                .nickname("testNickname")
                .name("testName")
                .build();
        }
    }

    @Nested
    class _memberAdd_$OAuthMemberRequest_$String_$HttpServletRequest_$HttpServletResponse {

        @Test
        void given_providerIsGoogle_then_invokeGoogleMemberService() throws IOException {
            // given
            String provider = "google";
            OAuthMemberRequest oAuthMemberRequest = oAuthMemberRequest();
            HttpServletRequest httpServletRequest = httpServletRequest();
            HttpServletResponse httpServletResponse = httpServletResponse();
            given(googleMemberService.addGoogleMember(oAuthMemberRequest, provider, httpServletRequest))
                .willReturn(1L);

            // when
            memberController.memberAdd(oAuthMemberRequest, provider,
                httpServletRequest,
                httpServletResponse);

            // then
            then(googleMemberService).should(times(1))
                .addGoogleMember(oAuthMemberRequest, provider, httpServletRequest);
            then(googleMemberService).shouldHaveNoMoreInteractions();
        }

        @Test
        void given_providerIsKakao_then_invokeKakaoMemberService() throws IOException {
            // given
            String provider = "kakao";
            OAuthMemberRequest oAuthMemberRequest = oAuthMemberRequest();
            HttpServletRequest httpServletRequest = httpServletRequest();
            HttpServletResponse httpServletResponse = httpServletResponse();
            given(kakaoMemberService.addKakaoMember(oAuthMemberRequest, provider, httpServletRequest))
                .willReturn(1L);

            // when
            memberController.memberAdd(oAuthMemberRequest, provider,
                httpServletRequest,
                httpServletResponse);

            // then
            then(kakaoMemberService).should(times(1))
                .addKakaoMember(oAuthMemberRequest, provider, httpServletRequest);
            then(kakaoMemberService).shouldHaveNoMoreInteractions();
        }

        @Test
        void given_providerIsNaver_then_invokeNaverMemberService() throws IOException {
            // given
            String provider = "naver";
            OAuthMemberRequest oAuthMemberRequest = oAuthMemberRequest();
            HttpServletRequest httpServletRequest = httpServletRequest();
            HttpServletResponse httpServletResponse = httpServletResponse();
            given(naverMemberService.addNaverMember(oAuthMemberRequest, provider, httpServletRequest))
                .willReturn(1L);

            // when
            memberController.memberAdd(oAuthMemberRequest, provider,
                httpServletRequest,
                httpServletResponse);

            // then
            then(naverMemberService).should(times(1))
                .addNaverMember(oAuthMemberRequest, provider, httpServletRequest);
            then(naverMemberService).shouldHaveNoMoreInteractions();
        }

        @Test
        void given_providerIsNotSupported_then_throwStatusCode400Exception() {
            // given
            String provider = "not_supported_case";
            OAuthMemberRequest oAuthMemberRequest = oAuthMemberRequest();
            HttpServletRequest httpServletRequest = httpServletRequest();
            HttpServletResponse httpServletResponse = httpServletResponse();

            // when
            Exception exception = catchException(() -> {
                    memberController.memberAdd(oAuthMemberRequest, provider,
                        httpServletRequest,
                        httpServletResponse);
                }
            );

            // then
            assertThat(((StatusCode400Exception) exception).getErrorCase())
                .isEqualTo(ErrorCase._400_NOT_SUPPORTED_CASE);
        }

        HttpServletRequest httpServletRequest() {
            return new MockHttpServletRequest();
        }

        HttpServletResponse httpServletResponse() {
            return new MockHttpServletResponse();
        }

        OAuthMemberRequest oAuthMemberRequest() {
            return OAuthMemberRequest.builder()
                .key("testKey")
                .nickname("testNickname")
                .profile("testProfile")
                .emailCheck(true)
                .build();
        }
    }


    @Nested
    class _memberModify_$ProfileUpdateRequest_$CustomUserDetails {

        @Test
        void given_request_then_responseHttpStatus204() {
            // given
            CustomUserDetails customUserDetails = WithMockCustomUserSecurityContextFactory.customUserDetailsMock();
            ProfileUpdateRequest profileUpdateRequest = profileUpdateRequest();

            // when
            ResponseEntity<Object> response = memberController.memberModify(profileUpdateRequest, customUserDetails);

            // then
            then(memberCrudService).should(times(1)).modifyMember(customUserDetails.getMember(), profileUpdateRequest);
            then(memberCrudService).shouldHaveNoMoreInteractions();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        }

        ProfileUpdateRequest profileUpdateRequest() {
            return ProfileUpdateRequest.builder()
                .profile("update_profile")
                .nickname("update_nickname")
                .name("update_name")
                .build();
        }
    }

    @Nested
    class _memberPasswordModify_$ChangePasswordRequest_$CustomUserDetails {

        @Test
        void given_request_then_responseHttpStatus204() {
            // given
            CustomUserDetails customUserDetails = WithMockCustomUserSecurityContextFactory.customUserDetailsMock();
            ChangePasswordRequest changePasswordRequest = changePasswordRequest();

            // when
            ResponseEntity<Object> response = memberController.memberPasswordModify(changePasswordRequest,
                customUserDetails);

            // then
            then(okkyMemberService).should(times(1)).modifyMember(customUserDetails.getMember(), changePasswordRequest);
            then(okkyMemberService).shouldHaveNoMoreInteractions();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        }

        ChangePasswordRequest changePasswordRequest() {
            return ChangePasswordRequest.builder()
                .oldPassword("old_password")
                .newPassword("new_password")
                .build();
        }
    }

    @Nested
    class _memberLogoutDateModify_$CustomUserDetails {

        @Test
        void given_request_then_responseHttpStatus204() {
            // given
            CustomUserDetails customUserDetails = WithMockCustomUserSecurityContextFactory.customUserDetailsMock();

            // when
            ResponseEntity<Object> response = memberController.memberLogoutDateModify(customUserDetails);

            // then
            then(memberCrudService).should(times(1))
                .modifyMemberLogoutDate(any(Member.class), any(LocalDateTime.class));
            then(memberCrudService).shouldHaveNoMoreInteractions();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        }
    }

    @Nested
    class _memberDetails_$CustomUserDetails {

        @Test
        void given_request_then_responseMemberDetailsResponse() {
            // given
            CustomUserDetails customUserDetails = WithMockCustomUserSecurityContextFactory.customUserDetailsMock();
            MemberDetailsResponse memberDetailsResponse = memberDetailsResponse(customUserDetails.getMember());
            given(memberCrudService.findMember(customUserDetails.getMember())).willReturn(memberDetailsResponse);

            // when
            ResponseEntity<MemberDetailsResponse> response = memberController.memberDetails(customUserDetails);

            // then
            then(memberCrudService).should(times(1)).findMember(customUserDetails.getMember());
            then(memberCrudService).shouldHaveNoMoreInteractions();
            assertThat(response.getBody()).isEqualTo(memberDetailsResponse);
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        }

        MemberDetailsResponse memberDetailsResponse(Member member) {
            return MemberDetailsResponse.from(member);
        }

    }

    @Nested
    class _memberRemove_$CustomUserDetails {

        @Test
        void given_request_then_responseHttpStatus204() {
            // given
            CustomUserDetails customUserDetails = WithMockCustomUserSecurityContextFactory.customUserDetailsMock();

            // when
            ResponseEntity<Object> response = memberController.memberRemove(customUserDetails);

            // then
            then(memberCrudService).should(times(1)).removeMember(customUserDetails.getMember());
            then(memberCrudService).shouldHaveNoMoreInteractions();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        }
    }

    @Nested
    class _authenticationMailSend_$UpdateEmailRequest_$CustomUserDetails {

        @Test
        void given_request_then_invokeSendAuthenticationMail() {
            // given
            UpdateEmailRequest updateEmailRequest = updateEmailRequest();
            CustomUserDetails customUserDetails = WithMockCustomUserSecurityContextFactory.customUserDetailsMock();

            // when
            memberController.authenticationMailSend(updateEmailRequest, customUserDetails);

            // then
            then(memberEmailService).should(times(1))
                .sendAuthenticationMail(updateEmailRequest, customUserDetails.getMember());
            then(memberEmailService).shouldHaveNoMoreInteractions();
        }

        UpdateEmailRequest updateEmailRequest() {
            return new UpdateEmailRequest("update_email");
        }

    }

    @Nested
    class _emailAuthenticate_$String {

        @Test
        void given_request_then_responseHttpStatus204() {
            // given
            String key = "testKey";

            // when
            ResponseEntity<Object> response = memberController.emailAuthenticate(key);

            // then
            then(memberCrudService).should(times(1)).modifyMemberRole(EncryptionUtil.base64Decode(key));
            then(memberCrudService).shouldHaveNoMoreInteractions();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        }
    }

    @Nested
    class _emailChangeAuthenticate_$String {

        @Test
        void given_request_then_responseHttpStatus204() {
            // given
            String key = "testKey";

            // when
            ResponseEntity<Object> response = memberController.emailChangeAuthenticate(key);

            // then
            then(memberCrudService).should(times(1)).modifyMemberRoleAndEmail(EncryptionUtil.base64Decode(key));
            then(memberCrudService).shouldHaveNoMoreInteractions();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        }

    }


}
