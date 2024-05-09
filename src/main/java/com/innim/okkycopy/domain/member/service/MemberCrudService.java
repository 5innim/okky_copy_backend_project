package com.innim.okkycopy.domain.member.service;

import com.innim.okkycopy.domain.member.dto.response.MemberDetailsResponse;
import com.innim.okkycopy.domain.member.entity.Member;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberCrudService {
    @PersistenceContext
    EntityManager entityManager;

    @Transactional(readOnly = true)
    public MemberDetailsResponse findMember(Member member) {
        Member mergedMember = entityManager.merge(member);

        return MemberDetailsResponse.from(mergedMember);
    }

}
