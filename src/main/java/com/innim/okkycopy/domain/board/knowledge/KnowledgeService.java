package com.innim.okkycopy.domain.board.knowledge;

import com.innim.okkycopy.domain.board.dto.request.write.WriteRequest;
import com.innim.okkycopy.domain.board.dto.response.post.detail.PostDetailResponse;
import com.innim.okkycopy.domain.board.dto.response.post.detail.PostRequesterInfoResponse;
import com.innim.okkycopy.domain.board.entity.BoardTopic;
import com.innim.okkycopy.domain.board.knowledge.entity.KnowledgePost;
import com.innim.okkycopy.domain.board.knowledge.repository.KnowledgePostRepository;
import com.innim.okkycopy.domain.board.repository.BoardTopicRepository;
import com.innim.okkycopy.domain.board.repository.PostHateRepository;
import com.innim.okkycopy.domain.board.repository.PostLikeRepository;
import com.innim.okkycopy.domain.board.repository.ScrapRepository;
import com.innim.okkycopy.domain.member.MemberRepository;
import com.innim.okkycopy.domain.member.entity.Member;
import com.innim.okkycopy.global.auth.CustomUserDetails;
import com.innim.okkycopy.global.error.ErrorCode;
import com.innim.okkycopy.global.error.exception.NoAuthorityException;
import com.innim.okkycopy.global.error.exception.NoSuchPostException;
import com.innim.okkycopy.global.error.exception.NoSuchTopicException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class KnowledgeService {

    private final BoardTopicRepository boardTopicRepository;
    private final KnowledgePostRepository knowledgePostRepository;
    private final MemberRepository memberRepository;
    private final ScrapRepository scrapRepository;
    private final PostLikeRepository postLikeRepository;
    private final PostHateRepository postHateRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void saveKnowledgePost(WriteRequest writeRequest, CustomUserDetails customUserDetails) {
        Member member = entityManager.merge(customUserDetails.getMember());

        BoardTopic boardTopic = boardTopicRepository.findByName(writeRequest.getTopic()).orElseThrow(() -> new NoSuchTopicException(
            ErrorCode._400_NO_SUCH_TOPIC));

        KnowledgePost knowledgePost = KnowledgePost.createKnowledgePost(writeRequest, boardTopic, member);
        entityManager.persist(knowledgePost);
    }

    public PostDetailResponse selectKnowledgePost(CustomUserDetails customUserDetails, long postId) {
        KnowledgePost knowledgePost = knowledgePostRepository.findByPostId(postId).orElseThrow(() -> new NoSuchPostException(ErrorCode._400_NO_SUCH_POST));
        Member member = memberRepository.findByMemberId(knowledgePost.getMember().getMemberId()).orElseGet(() -> Member.builder().memberId(0L)
            .build());

        PostDetailResponse response = PostDetailResponse.toPostDetailResponseDto(knowledgePost, member);
        if (customUserDetails != null) {
            Member requester = customUserDetails.getMember();
            response.setPostRequesterInfoResponse(
                    PostRequesterInfoResponse.builder()
                            .isScraped(scrapRepository.findByMemberAndPost(knowledgePost, requester).isPresent())
                            .isLiked(postLikeRepository.findByMemberAndPost(knowledgePost, requester).isPresent())
                            .isHated(postHateRepository.findByMemberAndPost(knowledgePost, requester).isPresent())
                            .build()
            );
        }

        return response;
    }

    @Transactional
    public void updateKnowledgePost(CustomUserDetails customUserDetails, WriteRequest updateRequest, long postId) {
        Member mergedMember = entityManager.merge(customUserDetails.getMember());
        KnowledgePost knowledgePost = knowledgePostRepository.findByPostId(postId).orElseThrow(() -> new NoSuchPostException(ErrorCode._400_NO_SUCH_POST));
        BoardTopic boardTopic = boardTopicRepository.findByName(updateRequest.getTopic()).orElseThrow(() -> new NoSuchTopicException(
            ErrorCode._400_NO_SUCH_TOPIC));

        if (knowledgePost.getMember().getMemberId() != mergedMember.getMemberId()) throw new NoAuthorityException(ErrorCode._403_NO_AUTHORITY);
        knowledgePost.updateKnowledgePost(updateRequest, boardTopic);
    }

    @Transactional
    public void deleteKnowledgePost(CustomUserDetails customUserDetails, long postId) {
        Member mergedMember = entityManager.merge(customUserDetails.getMember());
        KnowledgePost knowledgePost = knowledgePostRepository.findByPostId(postId).orElseThrow(() -> new NoSuchPostException(ErrorCode._400_NO_SUCH_POST));

        if (knowledgePost.getMember().getMemberId() != mergedMember.getMemberId()) throw new NoAuthorityException(ErrorCode._403_NO_AUTHORITY);
        entityManager.remove(knowledgePost);
    }
}
