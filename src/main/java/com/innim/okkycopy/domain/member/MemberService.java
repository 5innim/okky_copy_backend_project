package com.innim.okkycopy.domain.member;

import com.innim.okkycopy.domain.board.entity.Scrap;
import com.innim.okkycopy.domain.member.dto.request.MemberRequest;
import com.innim.okkycopy.domain.member.dto.response.MemberBriefResponse;
import com.innim.okkycopy.domain.member.dto.response.MemberDetailsResponse;
import com.innim.okkycopy.domain.member.entity.Member;
import com.innim.okkycopy.global.error.ErrorCase;
import com.innim.okkycopy.global.error.exception.StatusCode409Exception;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.SERIALIZABLE)
    public MemberBriefResponse addMember(MemberRequest memberRequest) {

        if (memberRepository.existsById(memberRequest.getId())) {
            throw new StatusCode409Exception(ErrorCase._409_DUPLICATE_ID);
        }

        if (memberRepository.existsByEmail(memberRequest.getEmail())) {
            throw new StatusCode409Exception(ErrorCase._409_DUPLICATE_EMAIL);
        }

        memberRequest.encodePassword(passwordEncoder);

        Member member = Member.from(memberRequest);
        memberRepository.save(member);

        return MemberBriefResponse.from(member);
    }

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
