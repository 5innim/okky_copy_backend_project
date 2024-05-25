package com.innim.okkycopy.domain.member.service;

import com.innim.okkycopy.domain.member.dto.request.OAuthMemberRequest;
import com.innim.okkycopy.domain.member.entity.GoogleMember;
import com.innim.okkycopy.domain.member.repository.GoogleMemberRepository;
import com.innim.okkycopy.global.auth.CustomOAuth2User;
import com.innim.okkycopy.global.error.ErrorCase;
import com.innim.okkycopy.global.error.exception.StatusCode400Exception;
import com.innim.okkycopy.global.error.exception.StatusCode401Exception;
import com.innim.okkycopy.global.util.email.MailUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GoogleMemberService {

    private final GoogleMemberRepository googleMemberRepository;
    private final MailUtil mailUtil;

    @Transactional(readOnly = true)
    public GoogleMember findGoogleMember(String providerId) {
        Optional<GoogleMember> googleMember = googleMemberRepository.findByProviderId(providerId);
        return googleMember.orElse(null);
    }

    @Transactional
    public Long addGoogleMember(OAuthMemberRequest oAuthMemberRequest, String provider, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            throw new StatusCode401Exception(ErrorCase._401_NOT_EXIST_SESSION);
        }

        Object value = session.getAttribute(oAuthMemberRequest.getKey());
        if (value == null) {
            throw new StatusCode400Exception(ErrorCase._400_BAD_FORM_DATA);
        }

        CustomOAuth2User oAuth2User = (CustomOAuth2User) value;
        if (!oAuth2User.getRegistrationId().equals(provider)) {
            throw new StatusCode400Exception(ErrorCase._400_NO_ACCEPTABLE_PARAMETER);
        }

        GoogleMember member = googleMemberRepository.save(GoogleMember.of(oAuthMemberRequest, oAuth2User));
        mailUtil.sendAuthenticateEmailAndPutCache(member.findEmail(), member.getMemberId(), member.getName());

        return member.getMemberId();
    }

}
