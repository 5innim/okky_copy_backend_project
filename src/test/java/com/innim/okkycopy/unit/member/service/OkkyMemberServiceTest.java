package com.innim.okkycopy.unit.member.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.innim.okkycopy.common.WithMockCustomUserSecurityContextFactory;
import com.innim.okkycopy.domain.member.dto.request.ChangePasswordRequest;
import com.innim.okkycopy.domain.member.dto.request.MemberRequest;
import com.innim.okkycopy.domain.member.dto.response.MemberBriefResponse;
import com.innim.okkycopy.domain.member.entity.GoogleMember;
import com.innim.okkycopy.domain.member.entity.Member;
import com.innim.okkycopy.domain.member.entity.OkkyMember;
import com.innim.okkycopy.domain.member.repository.MemberRepository;
import com.innim.okkycopy.domain.member.repository.OkkyMemberRepository;
import com.innim.okkycopy.domain.member.service.OkkyMemberService;
import com.innim.okkycopy.global.auth.enums.Role;
import com.innim.okkycopy.global.error.ErrorCase;
import com.innim.okkycopy.global.error.exception.StatusCode400Exception;
import com.innim.okkycopy.global.error.exception.StatusCode401Exception;
import com.innim.okkycopy.global.error.exception.StatusCode409Exception;
import com.innim.okkycopy.global.common.email.MailManager;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class OkkyMemberServiceTest {

    @Mock
    PasswordEncoder passwordEncoder;
    @Mock
    OkkyMemberRepository okkyMemberRepository;
    @Mock
    MemberRepository memberRepository;
    @Mock
    MailManager mailManager;
    @InjectMocks
    OkkyMemberService okkyMemberService;

    @Nested
    class _addMember_$MemberRequest {

        @Test
        void given_duplicateId_then_throwErrorCase409001() {
            // given
            MemberRequest memberRequest = memberRequest();
            given(okkyMemberRepository.existsById(memberRequest.getId())).willReturn(true);

            // when
            Exception exception = catchException(() -> {
                okkyMemberService.addMember(memberRequest);
            });

            // then
            then(okkyMemberRepository).should(times(1)).existsById(memberRequest.getId());
            then(okkyMemberRepository).shouldHaveNoMoreInteractions();
            assertThat(exception).isInstanceOf(StatusCode409Exception.class);
            assertThat(((StatusCode409Exception) exception).getErrorCase()).isEqualTo(ErrorCase._409_DUPLICATE_ID);
        }

        @Test
        void given_duplicateEmail_then_throwErrorCase409002() {
            // given
            MemberRequest memberRequest = memberRequest();
            given(okkyMemberRepository.existsById(memberRequest.getId())).willReturn(false);
            given(okkyMemberRepository.existsByEmail(memberRequest.getEmail())).willReturn(true);

            // when
            Exception exception = catchException(() -> {
                okkyMemberService.addMember(memberRequest);
            });

            // then
            then(okkyMemberRepository).should(times(1)).existsById(memberRequest.getId());
            then(okkyMemberRepository).should(times(1)).existsByEmail(memberRequest.getEmail());
            then(okkyMemberRepository).shouldHaveNoMoreInteractions();
            assertThat(exception).isInstanceOf(StatusCode409Exception.class);
            assertThat(((StatusCode409Exception) exception).getErrorCase()).isEqualTo(ErrorCase._409_DUPLICATE_EMAIL);
        }

        @Test
        void given_invoke_then_saveAndSendEmail() {
            // given
            MemberRequest memberRequest = memberRequest();
            given(okkyMemberRepository.existsById(memberRequest.getId())).willReturn(false);
            given(okkyMemberRepository.existsByEmail(memberRequest.getEmail())).willReturn(false);
            given(passwordEncoder.encode(memberRequest.getPassword())).willReturn("encoded_password");
            given(okkyMemberRepository.save(any(OkkyMember.class))).will(var -> {
                OkkyMember okkyMember = var.getArgument(0);
                okkyMember.setMemberId(1L);
                return okkyMember;
            });

            // when
            MemberBriefResponse memberBriefResponse = okkyMemberService.addMember(memberRequest);

            // then
            then(okkyMemberRepository).should(times(1)).save(any(OkkyMember.class));
            then(okkyMemberRepository).shouldHaveNoMoreInteractions();
            then(mailManager).should(times(1))
                .sendAuthenticateEmailAndPutCache(any(String.class), any(Long.class), any(String.class));
            then(mailManager).shouldHaveNoMoreInteractions();
            assertThat(memberBriefResponse.getEmail()).isEqualTo(memberRequest.getEmail());
            assertThat(memberBriefResponse.getNickname()).isEqualTo(memberRequest.getNickname());
            assertThat(memberBriefResponse.getName()).isEqualTo(memberRequest.getName());

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
    }

    @Nested
    class _modifyMember_$Member_$ChangePasswordRequest {

        @Test
        void given_noExistMember_then_throwErrorCase401005() {
            // given
            Member member = WithMockCustomUserSecurityContextFactory.customUserDetailsMock().getMember();
            ChangePasswordRequest changePasswordRequest = changePasswordRequest();
            given(memberRepository.findByMemberId(member.getMemberId())).willReturn(Optional.empty());

            // when
            Exception exception = catchException(() -> {
                    okkyMemberService.modifyMember(member, changePasswordRequest);
                }
            );

            // then
            then(memberRepository).should(times(1)).findByMemberId(member.getMemberId());
            then(memberRepository).shouldHaveNoMoreInteractions();
            assertThat(exception).isInstanceOf(StatusCode401Exception.class);
            assertThat(((StatusCode401Exception) exception).getErrorCase()).isEqualTo(ErrorCase._401_NO_SUCH_MEMBER);
        }

        @Test
        void given_notOkkyMember_then_throwErrorCase400026() {
            // given
            Member member = GoogleMember.builder()
                .memberId(1L)
                .email("test@test.com")
                .nickname("testNickname")
                .name("testName")
                .role(Role.USER)
                .build();
            ChangePasswordRequest changePasswordRequest = changePasswordRequest();
            given(memberRepository.findByMemberId(member.getMemberId())).willReturn(Optional.of(member));

            // when
            Exception exception = catchException(() -> {
                    okkyMemberService.modifyMember(member, changePasswordRequest);
                }
            );

            // then
            then(memberRepository).should(times(1)).findByMemberId(member.getMemberId());
            then(memberRepository).shouldHaveNoMoreInteractions();
            assertThat(exception).isInstanceOf(StatusCode400Exception.class);
            assertThat(((StatusCode400Exception) exception).getErrorCase()).isEqualTo(
                ErrorCase._400_NOT_SUPPORTED_CASE);
        }

        @Test
        void given_notCorrectPassword_then_throwErrorCase401008() {
            // given
            Member member = WithMockCustomUserSecurityContextFactory.customUserDetailsMock().getMember();
            ChangePasswordRequest changePasswordRequest = changePasswordRequest();
            given(memberRepository.findByMemberId(member.getMemberId())).willReturn(Optional.of(member));
            given(passwordEncoder.matches(any(String.class), any(String.class))).willReturn(false);

            // when
            Exception exception = catchException(() -> {
                    okkyMemberService.modifyMember(member, changePasswordRequest);
                }
            );

            // then
            then(memberRepository).should(times(1)).findByMemberId(member.getMemberId());
            then(memberRepository).shouldHaveNoMoreInteractions();
            then(passwordEncoder).should(times(1)).matches(any(String.class), any(String.class));
            then(passwordEncoder).shouldHaveNoMoreInteractions();
            assertThat(exception).isInstanceOf(StatusCode401Exception.class);
            assertThat(((StatusCode401Exception) exception).getErrorCase()).isEqualTo(
                ErrorCase._401_NOT_CORRECT_PASSWORD);
        }

        @Test
        void given_notChangedPassword_then_throwErrorCase400032() {
            // given
            Member member = WithMockCustomUserSecurityContextFactory.customUserDetailsMock().getMember();
            ChangePasswordRequest changePasswordRequest = changePasswordRequest();
            changePasswordRequest.setNewPassword("old_password");
            given(memberRepository.findByMemberId(member.getMemberId())).willReturn(Optional.of(member));
            given(passwordEncoder.matches(any(String.class), any(String.class))).willReturn(true);

            // when
            Exception exception = catchException(() -> {
                    okkyMemberService.modifyMember(member, changePasswordRequest);
                }
            );

            // then
            then(memberRepository).should(times(1)).findByMemberId(member.getMemberId());
            then(memberRepository).shouldHaveNoMoreInteractions();
            then(passwordEncoder).should(times(1)).matches(any(String.class), any(String.class));
            then(passwordEncoder).shouldHaveNoMoreInteractions();
            assertThat(exception).isInstanceOf(StatusCode400Exception.class);
            assertThat(((StatusCode400Exception) exception).getErrorCase()).isEqualTo(
                ErrorCase._400_NO_CHANGE_PASSWORD);
        }

        @Test
        void given_correctRequest_then_updateLogoutDate() {
            // given
            Member member = WithMockCustomUserSecurityContextFactory.customUserDetailsMock().getMember();
            LocalDateTime logoutDate = member.getLogoutDate();
            ChangePasswordRequest changePasswordRequest = changePasswordRequest();
            given(memberRepository.findByMemberId(member.getMemberId())).willReturn(Optional.of(member));
            given(passwordEncoder.matches(any(String.class), any(String.class))).willReturn(true);

            // when
            okkyMemberService.modifyMember(member, changePasswordRequest);

            // then
            then(memberRepository).should(times(1)).findByMemberId(member.getMemberId());
            then(memberRepository).shouldHaveNoMoreInteractions();
            then(passwordEncoder).should(times(1)).matches(any(String.class), any(String.class));
            then(passwordEncoder).should(times(1)).encode(changePasswordRequest.getNewPassword());
            then(passwordEncoder).shouldHaveNoMoreInteractions();
            assertThat(logoutDate).isNotEqualTo(member.getLogoutDate());
        }

        ChangePasswordRequest changePasswordRequest() {
            return ChangePasswordRequest.builder()
                .oldPassword("old_password")
                .newPassword("new_password")
                .build();
        }
    }
}
