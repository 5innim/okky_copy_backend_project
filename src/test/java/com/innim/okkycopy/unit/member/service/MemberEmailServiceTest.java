package com.innim.okkycopy.unit.member.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.innim.okkycopy.common.WithMockCustomUserSecurityContextFactory;
import com.innim.okkycopy.domain.member.dto.request.UpdateEmailRequest;
import com.innim.okkycopy.domain.member.entity.Member;
import com.innim.okkycopy.domain.member.repository.GoogleMemberRepository;
import com.innim.okkycopy.domain.member.repository.KakaoMemberRepository;
import com.innim.okkycopy.domain.member.repository.MemberRepository;
import com.innim.okkycopy.domain.member.repository.NaverMemberRepository;
import com.innim.okkycopy.domain.member.repository.OkkyMemberRepository;
import com.innim.okkycopy.domain.member.service.MemberEmailService;
import com.innim.okkycopy.global.auth.enums.Role;
import com.innim.okkycopy.global.error.ErrorCase;
import com.innim.okkycopy.global.error.exception.StatusCode400Exception;
import com.innim.okkycopy.global.error.exception.StatusCode401Exception;
import com.innim.okkycopy.global.error.exception.StatusCode403Exception;
import com.innim.okkycopy.global.util.email.MailUtil;
import java.util.Optional;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class MemberEmailServiceTest {

    @Mock
    OkkyMemberRepository okkyMemberRepository;
    @Mock
    GoogleMemberRepository googleMemberRepository;
    @Mock
    NaverMemberRepository naverMemberRepository;
    @Mock
    KakaoMemberRepository kakaoMemberRepository;
    @Mock
    MemberRepository memberRepository;
    @Mock
    MailUtil mailUtil;
    @InjectMocks
    MemberEmailService memberEmailService;

    @Nested
    class _sendAuthenticationMail_$UpdateEmailRequest_$Member {

        @Test
        void given_noExistMember_then_throwErrorCase401005() {
            // given
            UpdateEmailRequest updateEmailRequest = updateEmailRequest();
            Member member = WithMockCustomUserSecurityContextFactory.customUserDetailsMock().getMember();
            given(memberRepository.findByMemberId(member.getMemberId())).willReturn(Optional.empty());

            // when
            Exception exception = catchException(() -> {
                memberEmailService.sendAuthenticationMail(updateEmailRequest, member);
            });

            // then
            assertThat(exception).isInstanceOf(StatusCode401Exception.class);
            assertThat(((StatusCode401Exception) exception).getErrorCase()).isEqualTo(ErrorCase._401_NO_SUCH_MEMBER);
        }

        @Test
        void given_authenticateRequestAndAlreadyAuthenticatedMail_then_throwErrorCase403003() {
            // given
            UpdateEmailRequest updateEmailRequest = updateEmailRequest();
            Member member = WithMockCustomUserSecurityContextFactory.customUserDetailsMock().getMember();
            updateEmailRequest.setEmail(member.findEmail());
            given(memberRepository.findByMemberId(member.getMemberId())).willReturn(Optional.of(member));

            // when
            Exception exception = catchException(() -> {
                memberEmailService.sendAuthenticationMail(updateEmailRequest, member);
            });

            // then
            assertThat(exception).isInstanceOf(StatusCode403Exception.class);
            assertThat(((StatusCode403Exception) exception).getErrorCase()).isEqualTo(ErrorCase._403_MAIL_ALREADY_AUTHENTICATED);

        }

        @Test
        void given_changeMailRequestAndAlreadyExistMail_then_throwErrorCase400033() {
            // given
            UpdateEmailRequest updateEmailRequest = updateEmailRequest();
            Member member = WithMockCustomUserSecurityContextFactory.customUserDetailsMock().getMember();
            given(memberRepository.findByMemberId(member.getMemberId())).willReturn(Optional.of(member));
            given(okkyMemberRepository.existsByEmail(updateEmailRequest.getEmail())).willReturn(true);

            // when
            Exception exception = catchException(() -> {
                memberEmailService.sendAuthenticationMail(updateEmailRequest, member);
            });

            // then
            assertThat(exception).isInstanceOf(StatusCode400Exception.class);
            assertThat(((StatusCode400Exception) exception).getErrorCase()).isEqualTo(ErrorCase._400_IN_USAGE_EMAIL);

        }

        @Test
        void given_correctMailAuthenticateRequest_then_sendMail() {
            // given
            UpdateEmailRequest updateEmailRequest = updateEmailRequest();
            Member member = WithMockCustomUserSecurityContextFactory.customUserDetailsMock().getMember();
            updateEmailRequest.setEmail(member.findEmail());
            member.setRole(Role.MAIL_INVALID_USER);
            given(memberRepository.findByMemberId(member.getMemberId())).willReturn(Optional.of(member));

            // when
            memberEmailService.sendAuthenticationMail(updateEmailRequest, member);

            // then
            then(memberRepository).should(times(1)).findByMemberId(member.getMemberId());
            then(memberRepository).shouldHaveNoMoreInteractions();
            then(mailUtil).should(times(1)).sendAuthenticateChangedEmailAndPutCache(updateEmailRequest.getEmail(),
                member.getMemberId(),
                false);
            then(mailUtil).shouldHaveNoMoreInteractions();
        }

        @Test
        void given_correctChangeMailAuthenticateRequest_then_sendMail() {
            // given
            UpdateEmailRequest updateEmailRequest = updateEmailRequest();
            Member member = WithMockCustomUserSecurityContextFactory.customUserDetailsMock().getMember();
            given(memberRepository.findByMemberId(member.getMemberId())).willReturn(Optional.of(member));
            given(okkyMemberRepository.existsByEmail(updateEmailRequest.getEmail())).willReturn(false);

            // when
            memberEmailService.sendAuthenticationMail(updateEmailRequest, member);

            // then
            then(memberRepository).should(times(1)).findByMemberId(member.getMemberId());
            then(memberRepository).shouldHaveNoMoreInteractions();
            then(okkyMemberRepository).should(times(1)).existsByEmail(updateEmailRequest.getEmail());
            then(okkyMemberRepository).shouldHaveNoMoreInteractions();
            then(mailUtil).should(times(1)).sendAuthenticateChangedEmailAndPutCache(updateEmailRequest.getEmail(),
                member.getMemberId(),
                true);
            then(mailUtil).shouldHaveNoMoreInteractions();

        }

        UpdateEmailRequest updateEmailRequest() {
            return new UpdateEmailRequest("update_email");
        }
    }
}
