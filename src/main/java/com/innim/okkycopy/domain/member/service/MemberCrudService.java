package com.innim.okkycopy.domain.member.service;

import com.innim.okkycopy.domain.member.dto.request.ProfileUpdateRequest;
import com.innim.okkycopy.domain.member.dto.response.MemberDetailsResponse;
import com.innim.okkycopy.domain.member.entity.Member;
import com.innim.okkycopy.domain.member.repository.MemberRepository;
import com.innim.okkycopy.global.auth.enums.Role;
import com.innim.okkycopy.global.error.ErrorCase;
import com.innim.okkycopy.global.error.exception.StatusCode401Exception;
import com.innim.okkycopy.global.error.exception.StatusCode500Exception;
import com.innim.okkycopy.global.error.exception.StatusCodeException;
import com.innim.okkycopy.global.util.EncryptionUtil;
import com.innim.okkycopy.global.util.email.EmailAuthenticateValue;
import com.innim.okkycopy.global.util.email.MailUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpStatusCodeException;

@Service
@RequiredArgsConstructor
public class MemberCrudService {

    private final MemberRepository memberRepository;
    private final MailUtil mailUtil;
    @PersistenceContext
    EntityManager entityManager;

    @Transactional(readOnly = true)
    public MemberDetailsResponse findMember(Member member) {
        Member mergedMember = entityManager.merge(member);

        return MemberDetailsResponse.from(mergedMember);
    }

    @Transactional
    public void modifyMemberLoginDate(long memberId, LocalDateTime loginDate)
        throws StatusCodeException {
        Optional<Member> optionalMember = memberRepository.findByMemberId(memberId);
        Member member = optionalMember.orElseThrow(
            () -> new StatusCode401Exception(ErrorCase._401_NO_SUCH_MEMBER));
        member.setLoginDate(loginDate);
    }

    @Transactional
    public void modifyMemberIsEmailValid(String key) {
        EmailAuthenticateValue value = mailUtil.findValue(key).orElseThrow(
            () -> new StatusCode401Exception(ErrorCase._401_NO_SUCH_KEY));

        Member member = memberRepository.findByMemberId(value.getMemberId()).orElseThrow(
            () -> new StatusCode401Exception(ErrorCase._401_NO_SUCH_MEMBER));

        if (member.getRole() != Role.MAIL_INVALID_USER) {
            throw new StatusCode401Exception(ErrorCase._401_MAIL_ALREADY_AUTHENTICATED);
        };

        try {
            String generatedKey = EncryptionUtil.encryptWithSHA256(
                EncryptionUtil.connectStrings(member.findEmail(), member.getMemberId().toString()));
            if (!key.equals(generatedKey)) {
                throw new StatusCode401Exception(ErrorCase._401_KEY_VALIDATION_FAIL);
            }
        } catch(HttpStatusCodeException ex) {
            throw ex;
        } catch(Exception ex) {
            throw new StatusCode500Exception(ErrorCase._500_KEY_GENERATE_FAIL);
        }

        member.setRole(Role.USER);
        mailUtil.removeKey(key);

    }

    @Transactional
    public void modifyMember(Member member, ProfileUpdateRequest profileUpdateRequest) {
        Member mergedMember = entityManager.merge(member);
        mergedMember.setName(profileUpdateRequest.getName());
        mergedMember.setNickname(profileUpdateRequest.getNickname());
        mergedMember.setProfile(profileUpdateRequest.getProfile());

    }

    @Transactional
    public void modifyMemberLogoutDate(Member member, LocalDateTime logoutDate)
        throws StatusCodeException {
        Member mergedMember = entityManager.merge(member);
        mergedMember.setLogoutDate(logoutDate);
    }

    @Transactional(readOnly = true)
    public Member findMember(long memberId)
        throws StatusCodeException {
        Optional<Member> optionalMember = memberRepository.findByMemberId(memberId);
        return optionalMember.orElseThrow(
            () -> new StatusCode401Exception(ErrorCase._401_NO_SUCH_MEMBER));
    }

}
