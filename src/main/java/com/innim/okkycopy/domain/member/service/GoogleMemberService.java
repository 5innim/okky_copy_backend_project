package com.innim.okkycopy.domain.member.service;

import com.innim.okkycopy.domain.member.entity.GoogleMember;
import com.innim.okkycopy.domain.member.repository.GoogleMemberRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GoogleMemberService {

    private final GoogleMemberRepository googleMemberRepository;

    @Transactional(readOnly = true)
    public GoogleMember findGoogleMember(String email) {
        Optional<GoogleMember> googleMember = googleMemberRepository.findByEmail(email);
        return googleMember.orElse(null);
    }

}
