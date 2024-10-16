package com.innim.okkycopy.domain.member.service;

import com.innim.okkycopy.domain.member.dto.request.ProfileUpdateRequest;
import com.innim.okkycopy.domain.member.dto.response.MemberDetailsResponse;
import com.innim.okkycopy.domain.member.entity.Member;
import com.innim.okkycopy.domain.member.repository.MemberRepository;
import com.innim.okkycopy.global.auth.enums.Role;
import com.innim.okkycopy.global.common.email.EmailAuthenticateValue;
import com.innim.okkycopy.global.common.email.MailManager;
import com.innim.okkycopy.global.common.storage.image_usage.ImageUsageService;
import com.innim.okkycopy.global.error.ErrorCase;
import com.innim.okkycopy.global.error.exception.StatusCode401Exception;
import com.innim.okkycopy.global.error.exception.StatusCode403Exception;
import com.innim.okkycopy.global.error.exception.StatusCode500Exception;
import com.innim.okkycopy.global.error.exception.StatusCodeException;
import com.innim.okkycopy.global.util.EncryptionUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberCrudService {

    private final MemberRepository memberRepository;
    private final MailManager mailManager;
    private final ImageUsageService imageUsageService;

    @PersistenceContext
    private final EntityManager entityManager;

    @Transactional(readOnly = true)
    public MemberDetailsResponse findMember(Member requester) {
        Member member = memberRepository.findByMemberId(requester.getMemberId()).orElseThrow(
            () -> new StatusCode401Exception(ErrorCase._401_NO_SUCH_MEMBER)
        );

        return MemberDetailsResponse.from(member);
    }

    @Transactional(readOnly = true)
    public Member findMember(long memberId) {
        return memberRepository.findByMemberId(memberId).orElseThrow(
            () -> new StatusCode401Exception(ErrorCase._401_NO_SUCH_MEMBER)
        );
    }

    @Transactional
    public void removeMember(Member member) {
        try {
            imageUsageService.modifyImageUsage(member.getProfile(), false);
            member.remove(entityManager);
        } catch (IllegalArgumentException ex) {
            throw new StatusCode401Exception(ErrorCase._401_NO_SUCH_MEMBER);
        }
    }

    @Transactional
    public void modifyMemberLoginDate(long memberId, LocalDateTime loginDate) {
        Member member = memberRepository.findByMemberId(memberId).orElseThrow(
            () -> new StatusCode401Exception(ErrorCase._401_NO_SUCH_MEMBER));
        member.setLoginDate(loginDate);
    }

    @Transactional
    public void modifyMemberRole(String key) {
        key = EncryptionUtil.base64Decode(key);
        EmailAuthenticateValue value = mailManager.findValueByEmailAuthenticate(key).orElseThrow(
            () -> new StatusCode401Exception(ErrorCase._401_NO_SUCH_KEY));

        Member member = memberRepository.findByMemberId(value.getMemberId()).orElseThrow(
            () -> new StatusCode401Exception(ErrorCase._401_NO_SUCH_MEMBER));

        if (member.getRole() != Role.MAIL_INVALID_USER) {
            throw new StatusCode403Exception(ErrorCase._403_MAIL_ALREADY_AUTHENTICATED);
        }

        try {
            String generatedKey = EncryptionUtil.encryptWithSHA256(
                EncryptionUtil.connectStrings(member.findEmail(), member.getMemberId().toString(),
                    mailManager.getEncryptDivider()));
            if (!key.equals(generatedKey)) {
                throw new StatusCode401Exception(ErrorCase._401_KEY_VALIDATION_FAIL);
            }
        } catch (StatusCodeException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new StatusCode500Exception(ErrorCase._500_KEY_GENERATE_FAIL);
        }

        member.setRole(Role.USER);
        mailManager.removeKey(key);
    }

    @Transactional
    public void modifyMemberRoleAndEmail(String key) {
        key = EncryptionUtil.base64Decode(key);
        EmailAuthenticateValue value = mailManager.findValueByEmailChangeAuthenticate(key)
            .orElseThrow(() -> new StatusCode401Exception(ErrorCase._401_NO_SUCH_KEY));

        Member member = memberRepository.findByMemberId(value.getMemberId()).orElseThrow(
            () -> new StatusCode401Exception(ErrorCase._401_NO_SUCH_MEMBER));

        if (member.getRole() == Role.MAIL_INVALID_USER) {
            member.setRole(Role.USER);
        }
        member.changeEmail(value.getEmail());
        mailManager.removeKey(key);
    }

    @Transactional
    public void modifyMember(Member requester, ProfileUpdateRequest profileUpdateRequest) {
        Member member = memberRepository.findByMemberId(requester.getMemberId()).orElseThrow(
            () -> new StatusCode401Exception(ErrorCase._401_NO_SUCH_MEMBER)
        );
        member.setName(profileUpdateRequest.getName());
        member.setNickname(profileUpdateRequest.getNickname());

        imageUsageService.changeImage(member.getProfile(), profileUpdateRequest.getProfile());
        member.setProfile(profileUpdateRequest.getProfile());

    }

    @Transactional
    public void modifyMemberLogoutDate(Member requester) {
        Date logoutDate = new Date();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(logoutDate.toInstant(), ZoneId.systemDefault());

        Member member = memberRepository.findByMemberId(requester.getMemberId()).orElseThrow(
            () -> new StatusCode401Exception(ErrorCase._401_NO_SUCH_MEMBER)
        );
        member.setLogoutDate(localDateTime);
    }

}
