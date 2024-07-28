package com.innim.okkycopy.domain.member.service;

import com.innim.okkycopy.domain.member.dto.request.UpdateEmailRequest;
import com.innim.okkycopy.domain.member.entity.GoogleMember;
import com.innim.okkycopy.domain.member.entity.KakaoMember;
import com.innim.okkycopy.domain.member.entity.Member;
import com.innim.okkycopy.domain.member.entity.NaverMember;
import com.innim.okkycopy.domain.member.entity.OkkyMember;
import com.innim.okkycopy.domain.member.repository.GoogleMemberRepository;
import com.innim.okkycopy.domain.member.repository.KakaoMemberRepository;
import com.innim.okkycopy.domain.member.repository.MemberRepository;
import com.innim.okkycopy.domain.member.repository.NaverMemberRepository;
import com.innim.okkycopy.domain.member.repository.OkkyMemberRepository;
import com.innim.okkycopy.global.auth.enums.Role;
import com.innim.okkycopy.global.error.ErrorCase;
import com.innim.okkycopy.global.error.exception.StatusCode400Exception;
import com.innim.okkycopy.global.error.exception.StatusCode401Exception;
import com.innim.okkycopy.global.error.exception.StatusCode403Exception;
import com.innim.okkycopy.global.common.email.MailManager;
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
    private final MemberRepository memberRepository;
    private final MailManager mailManager;

    @Transactional(readOnly = true)
    public void sendAuthenticationMail(UpdateEmailRequest updateEmailRequest, Member requester) {
        Member member = memberRepository.findByMemberId(requester.getMemberId()).orElseThrow(
            () -> new StatusCode401Exception(ErrorCase._401_NO_SUCH_MEMBER)
        );
        if (member.findEmail().equals(updateEmailRequest.getEmail())) {
            if (member.getRole() != Role.MAIL_INVALID_USER) {
                throw new StatusCode403Exception(ErrorCase._403_MAIL_ALREADY_AUTHENTICATED);
            }

            mailManager.sendAuthenticateChangedEmailAndPutCache(
                updateEmailRequest.getEmail(),
                member.getMemberId(),
                false);

        } else { // TODO: Add case for new oAuth member
            boolean isExist = false;
            if (member instanceof OkkyMember) {
                isExist = okkyMemberRepository.existsByEmail(updateEmailRequest.getEmail());
            } else if (member instanceof GoogleMember) {
                isExist = googleMemberRepository.existsByEmail(updateEmailRequest.getEmail());
            } else if (member instanceof KakaoMember) {
                isExist = kakaoMemberRepository.existsByEmail(updateEmailRequest.getEmail());
            } else if (member instanceof NaverMember) {
                isExist = naverMemberRepository.existsByEmail(updateEmailRequest.getEmail());
            }

            if (isExist) {
                throw new StatusCode400Exception(ErrorCase._400_IN_USAGE_EMAIL);
            }

            mailManager.sendAuthenticateChangedEmailAndPutCache(
                updateEmailRequest.getEmail(),
                member.getMemberId(),
                true);
        }
    }
}
