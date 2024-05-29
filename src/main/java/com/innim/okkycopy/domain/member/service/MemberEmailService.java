package com.innim.okkycopy.domain.member.service;

import com.innim.okkycopy.domain.member.dto.request.UpdateEmailRequest;
import com.innim.okkycopy.domain.member.entity.GoogleMember;
import com.innim.okkycopy.domain.member.entity.KakaoMember;
import com.innim.okkycopy.domain.member.entity.Member;
import com.innim.okkycopy.domain.member.entity.NaverMember;
import com.innim.okkycopy.domain.member.entity.OkkyMember;
import com.innim.okkycopy.domain.member.repository.GoogleMemberRepository;
import com.innim.okkycopy.domain.member.repository.KakaoMemberRepository;
import com.innim.okkycopy.domain.member.repository.NaverMemberRepository;
import com.innim.okkycopy.domain.member.repository.OkkyMemberRepository;
import com.innim.okkycopy.global.auth.enums.Role;
import com.innim.okkycopy.global.error.ErrorCase;
import com.innim.okkycopy.global.error.exception.StatusCode401Exception;
import com.innim.okkycopy.global.util.email.MailUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberEmailService {

    private final OkkyMemberRepository okkyMemberRepository;
    private final GoogleMemberRepository googleMemberRepository;
    private final NaverMemberRepository naverMemberRepository;
    private final KakaoMemberRepository kakaoMemberRepository;
    private final MailUtil mailUtil;

    @PersistenceContext
    EntityManager entityManager;

    @Transactional(readOnly = true)
    public void sendAuthenticationMail(UpdateEmailRequest updateEmailRequest, Member member) {
        Member mergedMember = entityManager.merge(member);
        if (mergedMember.findEmail().equals(updateEmailRequest.getEmail())) {
            if (mergedMember.getRole() != Role.MAIL_INVALID_USER) {
                throw new StatusCode401Exception(ErrorCase._401_MAIL_ALREADY_AUTHENTICATED);
            }

            mailUtil.sendAuthenticateChangedEmailAndPutCache(
                updateEmailRequest.getEmail(),
                mergedMember.getMemberId(),
                false);

        } else { // TODO: Add case for new oAuth member
            boolean isExist = false;
            if (mergedMember instanceof OkkyMember) {
                isExist = okkyMemberRepository.existsByEmail(updateEmailRequest.getEmail());
            } else if (mergedMember instanceof GoogleMember) {
                isExist = googleMemberRepository.existsByEmail(updateEmailRequest.getEmail());
            } else if (mergedMember instanceof KakaoMember) {
                isExist = kakaoMemberRepository.existsByEmail(updateEmailRequest.getEmail());
            } else if (mergedMember instanceof NaverMember) {
                isExist = naverMemberRepository.existsByEmail(updateEmailRequest.getEmail());
            }

            if (isExist) {
                throw new StatusCode401Exception(ErrorCase._400_IN_USAGE_EMAIL);
            }

            mailUtil.sendAuthenticateChangedEmailAndPutCache(
                updateEmailRequest.getEmail(),
                mergedMember.getMemberId(),
                true);
        }
    }
}
