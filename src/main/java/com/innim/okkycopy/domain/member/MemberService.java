package com.innim.okkycopy.domain.member;

import com.innim.okkycopy.domain.board.entity.Scrap;
import com.innim.okkycopy.domain.member.dto.request.SignupRequest;
import com.innim.okkycopy.domain.member.dto.response.BriefMemberResponse;
import com.innim.okkycopy.domain.member.dto.response.MemberResponse;
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
    public BriefMemberResponse addMember(SignupRequest signupRequest) {

        if (memberRepository.existsById(signupRequest.getId())) {
            throw new StatusCode409Exception(ErrorCase._409_DUPLICATE_ID);
        }

        if (memberRepository.existsByEmail(signupRequest.getEmail())) {
            throw new StatusCode409Exception(ErrorCase._409_DUPLICATE_EMAIL);
        }

        signupRequest.encodePassword(passwordEncoder);

        Member member = Member.toMemberEntity(signupRequest);
        memberRepository.save(member);

        return BriefMemberResponse.toDto(member);
    }

    @Transactional(readOnly = true)
    public MemberResponse findMember(Member member) {
        Member mergedMember = entityManager.merge(member);

        List<Long> scrappedPostIdList = new ArrayList<>();
        List<Scrap> scraps = mergedMember.getScrapList();
        if (scraps != null) {
            for (Scrap scrap : scraps) {
                scrappedPostIdList.add(scrap.getPost().getPostId());
            }
        }

        return MemberResponse.builder()
            .memberId(mergedMember.getMemberId())
            .nickname(mergedMember.getNickname())
            .scrappedPost(scrappedPostIdList)
            .build();
    }

}
