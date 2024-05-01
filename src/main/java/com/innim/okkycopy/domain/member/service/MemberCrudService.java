package com.innim.okkycopy.domain.member.service;

import com.innim.okkycopy.domain.board.entity.Scrap;
import com.innim.okkycopy.domain.member.dto.response.MemberDetailsResponse;
import com.innim.okkycopy.domain.member.entity.Member;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;
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

        List<Long> scrappedPostIdList = new ArrayList<>();
        List<Scrap> scraps = mergedMember.getScrapList();
        if (scraps != null) {
            for (Scrap scrap : scraps) {
                scrappedPostIdList.add(scrap.getPost().getPostId());
            }
        }

        return MemberDetailsResponse.builder()
            .memberId(mergedMember.getMemberId())
            .nickname(mergedMember.getNickname())
            .scrappedPost(scrappedPostIdList)
            .build();
    }

}
