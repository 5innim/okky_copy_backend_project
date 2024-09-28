package com.innim.okkycopy.domain.board.knowledge;

import com.innim.okkycopy.domain.board.dto.request.write.PostRequest;
import com.innim.okkycopy.domain.board.dto.response.post.PostListResponse;
import com.innim.okkycopy.domain.board.dto.response.post.PostDetailsResponse;
import com.innim.okkycopy.domain.board.entity.BoardTopic;
import com.innim.okkycopy.domain.board.entity.PostExpression;
import com.innim.okkycopy.domain.board.knowledge.entity.KnowledgePost;
import com.innim.okkycopy.domain.board.knowledge.interfaces.KnowledgePostActionable;
import com.innim.okkycopy.domain.board.repository.BoardTopicRepository;
import com.innim.okkycopy.domain.board.repository.PostExpressionRepository;
import com.innim.okkycopy.domain.board.repository.ScrapRepository;
import com.innim.okkycopy.domain.member.entity.Member;
import com.innim.okkycopy.domain.member.repository.MemberRepository;
import com.innim.okkycopy.global.auth.CustomUserDetails;
import com.innim.okkycopy.global.common.storage.image_usage.ImageUsageService;
import com.innim.okkycopy.global.error.ErrorCase;
import com.innim.okkycopy.global.error.exception.StatusCode400Exception;
import com.innim.okkycopy.global.error.exception.StatusCode401Exception;
import com.innim.okkycopy.global.error.exception.StatusCode403Exception;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class KnowledgePostService {

    private final BoardTopicRepository boardTopicRepository;
    private final KnowledgePostRepository knowledgePostRepository;
    private final MemberRepository memberRepository;
    private final ScrapRepository scrapRepository;
    private final PostExpressionRepository postExpressionRepository;
    private final ImageUsageService imageUsageService;

    @PersistenceContext
    private final EntityManager entityManager;

    @Transactional
    public void addKnowledgePost(PostRequest postRequest, CustomUserDetails customUserDetails) {
        Member member = memberRepository.findByMemberId(customUserDetails.getUserId()).orElseThrow(
            () -> new StatusCode401Exception(ErrorCase._401_NO_SUCH_MEMBER)
        );

        BoardTopic boardTopic = boardTopicRepository.findByName(postRequest.getTopic())
            .orElseThrow(() -> new StatusCode400Exception(
                ErrorCase._400_NO_SUCH_TOPIC));

        imageUsageService.modifyImageUsages(postRequest.getContent(), true);
        KnowledgePost knowledgePost = KnowledgePost.of(postRequest, boardTopic, member);
        knowledgePostRepository.save(knowledgePost);
    }

    @Transactional
    public PostDetailsResponse findKnowledgePost(CustomUserDetails customUserDetails, long postId) {
        KnowledgePost knowledgePost = knowledgePostRepository.findByPostId(postId)
            .orElseThrow(() -> new StatusCode400Exception(ErrorCase._400_NO_SUCH_POST));

        PostDetailsResponse response;
        if (customUserDetails != null) {
            Member requester = customUserDetails.getMember();
            PostExpression postExpression = postExpressionRepository.findByMemberAndPost(knowledgePost, requester)
                .orElseGet(() -> null);
            response = PostDetailsResponse.from(knowledgePost, postExpression,
                scrapRepository.findByMemberAndPost(knowledgePost, requester).isPresent());
        } else {
            response = PostDetailsResponse.from(knowledgePost);
        }
        knowledgePost.increaseViews();

        return response;
    }

    @Transactional
    public void modifyKnowledgePost(CustomUserDetails customUserDetails, PostRequest updateRequest, long postId) {
        Member member = memberRepository.findByMemberId(customUserDetails.getUserId()).orElseThrow(
            () -> new StatusCode401Exception(ErrorCase._401_NO_SUCH_MEMBER)
        );
        KnowledgePost knowledgePost = knowledgePostRepository.findByPostId(postId)
            .orElseThrow(() -> new StatusCode400Exception(ErrorCase._400_NO_SUCH_POST));
        BoardTopic boardTopic = boardTopicRepository.findByName(updateRequest.getTopic())
            .orElseThrow(() -> new StatusCode400Exception(
                ErrorCase._400_NO_SUCH_TOPIC));

        if (knowledgePost.getMember() == null || knowledgePost.getMember().getMemberId() != member.getMemberId()) {
            throw new StatusCode403Exception(ErrorCase._403_NO_AUTHORITY);
        }
        imageUsageService.modifyImageUsages(knowledgePost.getContent(), updateRequest.getContent());
        ((KnowledgePostActionable) knowledgePost).update(updateRequest, boardTopic);
    }

    @Transactional
    public void removeKnowledgePost(CustomUserDetails customUserDetails, long postId) {
        Member member = memberRepository.findByMemberId(customUserDetails.getUserId()).orElseThrow(
            () -> new StatusCode401Exception(ErrorCase._401_NO_SUCH_MEMBER)
        );
        KnowledgePost knowledgePost = knowledgePostRepository.findByPostId(postId)
            .orElseThrow(() -> new StatusCode400Exception(ErrorCase._400_NO_SUCH_POST));

        if (knowledgePost.getMember() == null || knowledgePost.getMember().getMemberId() != member.getMemberId()) {
            throw new StatusCode403Exception(ErrorCase._403_NO_AUTHORITY);
        }
        imageUsageService.modifyImageUsages(knowledgePost.getContent(), false);
        knowledgePost.remove(entityManager);

    }

    @Transactional(readOnly = true)
    public PostListResponse findKnowledgePostsByTopicIdAndKeyword(Long topicId, String keyword, Pageable pageable) {
        Page<KnowledgePost> knowledgePostPage;
        if (topicId == null) {
            knowledgePostPage = knowledgePostRepository.findPageByKeyword((keyword == null) ? "" : keyword,
                pageable);
        } else {
            BoardTopic boardTopic = boardTopicRepository
                .findByTopicId(topicId)
                .orElseThrow(() -> new StatusCode400Exception(ErrorCase._400_NO_SUCH_TOPIC));
            if (KnowledgePost.isNotSupportedTopic(boardTopic)) {
                throw new StatusCode400Exception(ErrorCase._400_NOT_SUPPORTED_CASE);
            }
            knowledgePostPage = knowledgePostRepository
                .findPageByBoardTopicAndKeyword(boardTopic, (keyword == null) ? "" : keyword, pageable);
        }

        return PostListResponse.from(knowledgePostPage);
    }
}
