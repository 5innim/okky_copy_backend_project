package com.innim.okkycopy.domain.member.service;

import com.innim.okkycopy.domain.member.dto.request.ProfileUpdateRequest;
import com.innim.okkycopy.domain.member.dto.response.MemberDetailsResponse;
import com.innim.okkycopy.domain.member.entity.Member;
import com.innim.okkycopy.domain.member.repository.MemberRepository;
import com.innim.okkycopy.global.error.ErrorCase;
import com.innim.okkycopy.global.error.exception.StatusCode401Exception;
import com.innim.okkycopy.global.error.exception.StatusCodeException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberCrudService {

    private final MemberRepository memberRepository;
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
    public void modifyMember(Member member, ProfileUpdateRequest profileUpdateRequest) {
        Member mergedMember = entityManager.merge(member);
        mergedMember.setName(profileUpdateRequest.getName());
        mergedMember.setNickname(profileUpdateRequest.getNickname());
        mergedMember.setProfile(profileUpdateRequest.getProfile());

    }

    @Transactional
    public void modifyMemberLogoutDate(Member member, LocalDateTime loginDate)
        throws StatusCodeException {
        Member mergedMember = entityManager.merge(member);
        mergedMember.setLogoutDate(loginDate);
    }

    @Transactional(readOnly = true)
    public Member findMember(long memberId)
        throws StatusCodeException {
        Optional<Member> optionalMember = memberRepository.findByMemberId(memberId);
        return optionalMember.orElseThrow(
            () -> new StatusCode401Exception(ErrorCase._401_NO_SUCH_MEMBER));
    }

}
