package com.innim.okkycopy.domain.board.knowledge;

import com.innim.okkycopy.domain.board.dto.request.write.WriteRequest;
import com.innim.okkycopy.domain.board.dto.response.post.brief.PostsResponse;
import com.innim.okkycopy.domain.board.dto.response.post.detail.PostDetailResponse;
import com.innim.okkycopy.domain.board.dto.response.post.detail.PostRequesterInfoResponse;
import com.innim.okkycopy.domain.board.entity.BoardTopic;
import com.innim.okkycopy.domain.board.entity.PostExpression;
import com.innim.okkycopy.domain.board.enums.ExpressionType;
import com.innim.okkycopy.domain.board.knowledge.entity.KnowledgePost;
import com.innim.okkycopy.domain.board.knowledge.repository.KnowledgePostRepository;
import com.innim.okkycopy.domain.board.repository.BoardTopicRepository;
import com.innim.okkycopy.domain.board.repository.PostExpressionRepository;
import com.innim.okkycopy.domain.board.repository.ScrapRepository;
import com.innim.okkycopy.domain.member.MemberRepository;
import com.innim.okkycopy.domain.member.entity.Member;
import com.innim.okkycopy.global.auth.CustomUserDetails;
import com.innim.okkycopy.global.error.ErrorCode;
import com.innim.okkycopy.global.error.exception.NoAuthorityException;
import com.innim.okkycopy.global.error.exception.NoSuchPostException;
import com.innim.okkycopy.global.error.exception.NoSuchTopicException;
import com.innim.okkycopy.global.error.exception.NotSupportedCaseException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class KnowledgeService {

    private final BoardTopicRepository boardTopicRepository;
    private final KnowledgePostRepository knowledgePostRepository;
    private final MemberRepository memberRepository;
    private final ScrapRepository scrapRepository;
    private final PostExpressionRepository postExpressionRepository;

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

    @Transactional
    public PostDetailResponse selectKnowledgePost(CustomUserDetails customUserDetails, long postId) {
        KnowledgePost knowledgePost = knowledgePostRepository.findByPostId(postId).orElseThrow(() -> new NoSuchPostException(ErrorCode._400_NO_SUCH_POST));
        Member member = memberRepository.findByMemberId(knowledgePost.getMember().getMemberId()).orElseGet(() -> null);

        PostDetailResponse response = PostDetailResponse.toPostDetailResponseDto(knowledgePost, member);
        if (customUserDetails != null) {
            Member requester = customUserDetails.getMember();
            PostExpression postExpression = postExpressionRepository.findByMemberAndPost(knowledgePost, requester).orElseGet(() -> null);
            response.setPostRequesterInfoResponse(
                    PostRequesterInfoResponse.builder()
                            .scrap(scrapRepository.findByMemberAndPost(knowledgePost, requester).isPresent())
                            .like(postExpression != null && postExpression.getExpressionType().equals(ExpressionType.LIKE))
                            .hate(postExpression != null && postExpression.getExpressionType().equals(ExpressionType.HATE))
                            .build()
            );
        }
        knowledgePost.setViews(knowledgePost.getViews() + 1);

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

    @Transactional(readOnly = true)
    public PostsResponse selectKnowledgePostsByCondition(Long topicId, String keyword, Pageable pageable) {
        Page<KnowledgePost> knowledgePostPage;
        if (topicId == null) {
            knowledgePostPage = knowledgePostRepository.findAll((keyword == null) ? "":keyword, pageable);
        } else {
            BoardTopic boardTopic = boardTopicRepository
                    .findByTopicId(topicId)
                    .orElseThrow(() -> new NoSuchTopicException(ErrorCode._400_NO_SUCH_TOPIC));
            if (boardTopic.getBoardType().getTypeId() != 2) throw new NotSupportedCaseException(ErrorCode._400_NOT_SUPPORTED_CASE);
            knowledgePostPage = knowledgePostRepository.findByTopicId(boardTopic, (keyword == null) ? "":keyword, pageable);
        }

        return PostsResponse.createPostsResponse(knowledgePostPage);
    }
}
