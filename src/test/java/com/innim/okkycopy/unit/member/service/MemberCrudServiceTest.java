package com.innim.okkycopy.unit.member.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.innim.okkycopy.common.WithMockCustomUserSecurityContextFactory;
import com.innim.okkycopy.domain.member.dto.request.ProfileUpdateRequest;
import com.innim.okkycopy.domain.member.dto.response.MemberDetailsResponse;
import com.innim.okkycopy.domain.member.entity.Member;
import com.innim.okkycopy.domain.member.repository.MemberRepository;
import com.innim.okkycopy.domain.member.service.MemberCrudService;
import com.innim.okkycopy.global.auth.enums.Role;
import com.innim.okkycopy.global.error.ErrorCase;
import com.innim.okkycopy.global.error.exception.StatusCode401Exception;
import com.innim.okkycopy.global.util.EncryptionUtil;
import com.innim.okkycopy.global.util.email.EmailAuthenticateValue;
import com.innim.okkycopy.global.util.email.MailUtil;
import jakarta.persistence.EntityManager;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class MemberCrudServiceTest {

    @Mock
    MemberRepository memberRepository;
    @Mock
    MailUtil mailUtil;
    @Mock
    EntityManager entityManager;
    @InjectMocks
    MemberCrudService memberCrudService;

    @Nested
    class _findMember_$Member {

        @Test
        void given_existMember_then_responseMemberDetailsResponse() {
            // given
            Member member = WithMockCustomUserSecurityContextFactory.customUserDetailsMock().getMember();
            given(memberRepository.findByMemberId(member.getMemberId())).willReturn(Optional.of(member));

            // when
            MemberDetailsResponse memberDetailsResponse = memberCrudService.findMember(member);

            // then
            then(memberRepository).should(times(1)).findByMemberId(member.getMemberId());
            then(memberRepository).shouldHaveNoMoreInteractions();
            assertThat(memberDetailsResponse.getMemberId()).isEqualTo(member.getMemberId());
        }

        @Test
        void given_noExistMember_then_throwErrorCase401005() {
            // given
            Member member = WithMockCustomUserSecurityContextFactory.customUserDetailsMock().getMember();
            given(memberRepository.findByMemberId(member.getMemberId())).willReturn(Optional.empty());

            // when
            Exception exception = catchException(() -> {
                    memberCrudService.findMember(member);
                }
            );

            // then
            then(memberRepository).should(times(1)).findByMemberId(member.getMemberId());
            then(memberRepository).shouldHaveNoMoreInteractions();
            assertThat(exception).isInstanceOf(StatusCode401Exception.class);
            assertThat(((StatusCode401Exception) exception).getErrorCase()).isEqualTo(ErrorCase._401_NO_SUCH_MEMBER);
        }
    }

    @Nested
    class _removeMember_$Member {

        @Test
        void given_member_then_invokeEntityManagerMethods() {
            // given
            Member member = WithMockCustomUserSecurityContextFactory.customUserDetailsMock().getMember();
            member.setComments(Collections.emptyList());
            member.setPosts(Collections.emptyList());
            given(entityManager.merge(member)).willReturn(member);

            // when
            memberCrudService.removeMember(member);

            // then
            then(entityManager).should(times(1)).merge(member);
            then(entityManager).should(times(1)).remove(member);
            then(entityManager).shouldHaveNoMoreInteractions();
        }

        @Test
        void given_noExistMember_then_throwErrorCase401005() {
            // given
            Member member = WithMockCustomUserSecurityContextFactory.customUserDetailsMock().getMember();
            member.setComments(Collections.emptyList());
            member.setPosts(Collections.emptyList());
            given(entityManager.merge(member)).willThrow(IllegalArgumentException.class);

            // when
            Exception exception = catchException(() -> {
                memberCrudService.removeMember(member);
            });

            // then
            assertThat(exception).isInstanceOf(StatusCode401Exception.class);
            assertThat(((StatusCode401Exception) exception).getErrorCase()).isEqualTo(ErrorCase._401_NO_SUCH_MEMBER);
        }
    }

    @Nested
    class _modifyMemberLoginDate_$long_$LocalDateTime {

        @Test
        void given_noExistMemberId_then_throwErrorCase401005() {
            // given
            long memberId = 1L;
            Date loginDate = new Date();
            LocalDateTime localDateTime = LocalDateTime.ofInstant(loginDate.toInstant(), ZoneId.systemDefault());
            given(memberRepository.findByMemberId(memberId)).willReturn(Optional.empty());

            // when
            Exception exception = catchException(() -> {
                memberCrudService.modifyMemberLoginDate(memberId, localDateTime);
            });

            // then
            assertThat(exception).isInstanceOf(StatusCode401Exception.class);
            assertThat(((StatusCode401Exception) exception).getErrorCase()).isEqualTo(ErrorCase._401_NO_SUCH_MEMBER);
        }
    }

    @Nested
    class _modifyMemberRole_$String {

        @Test
        void given_noExistKey_then_throwErrorCase401009() {
            // given
            String key = "test_key";
            given(mailUtil.findValueByEmailAuthenticate(key)).willReturn(Optional.empty());

            // when
            Exception exception = catchException(() -> {
                memberCrudService.modifyMemberRole(key);
            });

            // then
            assertThat(exception).isInstanceOf(StatusCode401Exception.class);
            assertThat(((StatusCode401Exception) exception).getErrorCase()).isEqualTo(ErrorCase._401_NO_SUCH_KEY);
        }

        @Test
        void given_noExistMember_then_throwErrorCase401005() {
            // given
            String key = "test_key";
            EmailAuthenticateValue emailAuthenticateValue = emailAuthenticateValue();
            given(mailUtil.findValueByEmailAuthenticate(key)).willReturn(Optional.of(emailAuthenticateValue));
            given(memberRepository.findByMemberId(emailAuthenticateValue.getMemberId())).willReturn(Optional.empty());

            // when
            Exception exception = catchException(() -> {
                memberCrudService.modifyMemberRole(key);
            });

            // then
            assertThat(exception).isInstanceOf(StatusCode401Exception.class);
            assertThat(((StatusCode401Exception) exception).getErrorCase()).isEqualTo(ErrorCase._401_NO_SUCH_MEMBER);
        }

        @Test
        void given_memberIsAlreadyAuthenticated_then_throwErrorCase401010() {
            // given
            String key = "test_key";
            EmailAuthenticateValue emailAuthenticateValue = emailAuthenticateValue();
            Member member = WithMockCustomUserSecurityContextFactory.customUserDetailsMock().getMember();
            given(mailUtil.findValueByEmailAuthenticate(key)).willReturn(Optional.of(emailAuthenticateValue));
            given(memberRepository.findByMemberId(emailAuthenticateValue.getMemberId())).willReturn(
                Optional.of(member));

            // when
            Exception exception = catchException(() -> {
                memberCrudService.modifyMemberRole(key);
            });

            // then
            assertThat(exception).isInstanceOf(StatusCode401Exception.class);
            assertThat(((StatusCode401Exception) exception).getErrorCase()).isEqualTo(
                ErrorCase._401_MAIL_ALREADY_AUTHENTICATED);
        }

        @Test
        void given_validateKeyFail_then_throwErrorCase401011() {
            // given
            String key = "test_key";
            EmailAuthenticateValue emailAuthenticateValue = emailAuthenticateValue();
            Member member = WithMockCustomUserSecurityContextFactory.customUserDetailsMock().getMember();
            member.setRole(Role.MAIL_INVALID_USER);
            given(mailUtil.findValueByEmailAuthenticate(key)).willReturn(Optional.of(emailAuthenticateValue));
            given(memberRepository.findByMemberId(emailAuthenticateValue.getMemberId())).willReturn(
                Optional.of(member));

            // when
            Exception exception = catchException(() -> {
                memberCrudService.modifyMemberRole(key);
            });

            // then
            assertThat(exception).isInstanceOf(StatusCode401Exception.class);
            assertThat(((StatusCode401Exception) exception).getErrorCase()).isEqualTo(
                ErrorCase._401_KEY_VALIDATION_FAIL);
        }

        @Test
        void given_correctRequest_then_invokeSetRole() throws NoSuchAlgorithmException {
            // given
            Member member = WithMockCustomUserSecurityContextFactory.customUserDetailsMock().getMember();
            String key = EncryptionUtil.encryptWithSHA256(
                EncryptionUtil.connectStrings(member.findEmail(), member.getMemberId().toString()));
            ;
            EmailAuthenticateValue emailAuthenticateValue = emailAuthenticateValue();
            member.setRole(Role.MAIL_INVALID_USER);
            given(mailUtil.findValueByEmailAuthenticate(key)).willReturn(Optional.of(emailAuthenticateValue));
            given(memberRepository.findByMemberId(emailAuthenticateValue.getMemberId())).willReturn(
                Optional.of(member));

            // when
            memberCrudService.modifyMemberRole(key);

            // then
            assertThat(member.getRole()).isEqualTo(Role.USER);
            then(mailUtil).should(times(1)).removeKey(key);
            then(mailUtil).shouldHaveNoMoreInteractions();
        }


        EmailAuthenticateValue emailAuthenticateValue() {
            return new EmailAuthenticateValue(1L, "test@test.com");
        }
    }

    @Nested
    class _modifyMemberRoleAndEmail_$String {

        @Test
        void given_noExistKey_then_throwErrorCase401009() {
            // given
            String key = "test_key";
            given(mailUtil.findValueByEmailChangeAuthenticate(key)).willReturn(Optional.empty());

            // when
            Exception exception = catchException(() -> {
                memberCrudService.modifyMemberRoleAndEmail(key);
            });

            // then
            assertThat(exception).isInstanceOf(StatusCode401Exception.class);
            assertThat(((StatusCode401Exception) exception).getErrorCase()).isEqualTo(ErrorCase._401_NO_SUCH_KEY);
        }
        @Test
        void given_noExistMember_then_throwErrorCase401005() {
            // given
            String key = "test_key";
            EmailAuthenticateValue emailAuthenticateValue = emailAuthenticateValue();
            given(mailUtil.findValueByEmailChangeAuthenticate(key)).willReturn(Optional.of(emailAuthenticateValue));
            given(memberRepository.findByMemberId(emailAuthenticateValue.getMemberId())).willReturn(Optional.empty());

            // when
            Exception exception = catchException(() -> {
                memberCrudService.modifyMemberRoleAndEmail(key);
            });

            // then
            assertThat(exception).isInstanceOf(StatusCode401Exception.class);
            assertThat(((StatusCode401Exception) exception).getErrorCase()).isEqualTo(ErrorCase._401_NO_SUCH_MEMBER);
        }

        @Test
        void given_correctRequest_then_invokeChangeEmail() {
            // given
            String key = "test_key";
            Member member = WithMockCustomUserSecurityContextFactory.customUserDetailsMock().getMember();
            member.setRole(Role.MAIL_INVALID_USER);
            EmailAuthenticateValue emailAuthenticateValue = emailAuthenticateValue();
            given(mailUtil.findValueByEmailChangeAuthenticate(key)).willReturn(Optional.of(emailAuthenticateValue));
            given(memberRepository.findByMemberId(emailAuthenticateValue.getMemberId())).willReturn(Optional.of(member));

            // when
            memberCrudService.modifyMemberRoleAndEmail(key);

            // then
            assertThat(member.getRole()).isEqualTo(Role.USER);
            then(mailUtil).should(times(1)).removeKey(key);
            then(mailUtil).shouldHaveNoMoreInteractions();
        }

        EmailAuthenticateValue emailAuthenticateValue() {
            return new EmailAuthenticateValue(1L, "test2@test.com");
        }
    }

    @Nested
    class _modifyMember_$Member_$ProfileUpdateRequest {

        @Test
        void given_noExistMember_then_throwErrorCase401005() {
            // given
            Member member = WithMockCustomUserSecurityContextFactory.customUserDetailsMock().getMember();
            ProfileUpdateRequest profileUpdateRequest = profileUpdateRequest();
            given(memberRepository.findByMemberId(member.getMemberId())).willReturn(Optional.empty());

            // when
            Exception exception = catchException( () -> {
                memberCrudService.modifyMember(member, profileUpdateRequest);
            });

            // then
            then(memberRepository).should(times(1)).findByMemberId(member.getMemberId());
            then(memberRepository).shouldHaveNoMoreInteractions();
            assertThat(exception).isInstanceOf(StatusCode401Exception.class);
            assertThat(((StatusCode401Exception) exception).getErrorCase()).isEqualTo(ErrorCase._401_NO_SUCH_MEMBER);
        }

        @Test
        void given_correctRequest_then_invokeSetMethods() {
            // given
            Member member = WithMockCustomUserSecurityContextFactory.customUserDetailsMock().getMember();
            ProfileUpdateRequest profileUpdateRequest = profileUpdateRequest();
            given(memberRepository.findByMemberId(member.getMemberId())).willReturn(Optional.of(member));

            // when
            memberCrudService.modifyMember(member, profileUpdateRequest);

            // then
            assertThat(member.getName()).isEqualTo(profileUpdateRequest.getName());
            assertThat(member.getNickname()).isEqualTo(profileUpdateRequest.getNickname());
            assertThat(member.getProfile()).isEqualTo(profileUpdateRequest.getProfile());
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
    class _modifyMemberLogoutDate_$Member_$LocalDateTime {

        @Test
        void given_noExistMember_then_throwErrorCase401005() {
            // given
            Member member = WithMockCustomUserSecurityContextFactory.customUserDetailsMock().getMember();
            Date logoutDate = new Date();
            LocalDateTime localDateTime = LocalDateTime.ofInstant(logoutDate.toInstant(), ZoneId.systemDefault());
            given(memberRepository.findByMemberId(member.getMemberId())).willReturn(Optional.empty());

            // when
            Exception exception = catchException( () -> {
               memberCrudService.modifyMemberLogoutDate(member, localDateTime);
            });

            // then
            then(memberRepository).should(times(1)).findByMemberId(member.getMemberId());
            then(memberRepository).shouldHaveNoMoreInteractions();
            assertThat(exception).isInstanceOf(StatusCode401Exception.class);
            assertThat(((StatusCode401Exception) exception).getErrorCase()).isEqualTo(ErrorCase._401_NO_SUCH_MEMBER);
        }

        @Test
        void given_correctRequest_thenInvokeSerLogoutDate() {
            // given
            Member member = WithMockCustomUserSecurityContextFactory.customUserDetailsMock().getMember();
            Date logoutDate = new Date();
            LocalDateTime localDateTime = LocalDateTime.ofInstant(logoutDate.toInstant(), ZoneId.systemDefault());
            given(memberRepository.findByMemberId(member.getMemberId())).willReturn(Optional.of(member));

            // when
            memberCrudService.modifyMemberLogoutDate(member, localDateTime);

            // then
            then(memberRepository).should(times(1)).findByMemberId(member.getMemberId());
            then(memberRepository).shouldHaveNoMoreInteractions();
            assertThat(member.getLogoutDate()).isEqualTo(localDateTime);
        }
    }

    @Nested
    class _findMember_$long {

        @Test
        void given_noExistMember_then_throwErrorCase401005() {
            // given
            long memberId = 1L;
            given(memberRepository.findByMemberId(memberId)).willReturn(Optional.empty());

            // when
            Exception exception = catchException( () -> {
                memberCrudService.findMember(memberId);
            });

            // then
            assertThat(exception).isInstanceOf(StatusCode401Exception.class);
            assertThat(((StatusCode401Exception) exception).getErrorCase()).isEqualTo(ErrorCase._401_NO_SUCH_MEMBER);
        }
    }
}
