package com.innim.okkycopy.domain.board.community;

import com.innim.okkycopy.domain.board.community.entity.CommunityPost;
import com.innim.okkycopy.domain.board.dto.request.write.PostRequest;
import com.innim.okkycopy.domain.board.dto.response.post.detail.PostDetailsResponse;
import com.innim.okkycopy.domain.board.dto.response.post.detail.RequesterInfo;
import com.innim.okkycopy.domain.board.entity.BoardTopic;
import com.innim.okkycopy.domain.board.entity.PostExpression;
import com.innim.okkycopy.domain.board.enums.ExpressionType;
import com.innim.okkycopy.domain.board.repository.BoardTopicRepository;
import com.innim.okkycopy.domain.board.repository.PostExpressionRepository;
import com.innim.okkycopy.domain.board.repository.ScrapRepository;
import com.innim.okkycopy.domain.member.MemberRepository;
import com.innim.okkycopy.domain.member.entity.Member;
import com.innim.okkycopy.global.auth.CustomUserDetails;
import com.innim.okkycopy.global.error.ErrorCase;
import com.innim.okkycopy.global.error.exception.StatusCode400Exception;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommunityPostService {

    private final BoardTopicRepository boardTopicRepository;
    private final CommunityPostRepository communityPostRepository;
    private final MemberRepository memberRepository;
    private final PostExpressionRepository postExpressionRepository;
    private final ScrapRepository scrapRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void addCommunityPost(PostRequest postRequest, CustomUserDetails customUserDetails) {
        Member member = entityManager.merge(customUserDetails.getMember());

        BoardTopic boardTopic = boardTopicRepository.findByName(postRequest.getTopic())
            .orElseThrow(() -> new StatusCode400Exception(
                ErrorCase._400_NO_SUCH_TOPIC));
        CommunityPost communityPost = CommunityPost.of(postRequest, boardTopic, member);
        entityManager.persist(communityPost);
    }

    @Transactional
    public PostDetailsResponse findCommunityPost(CustomUserDetails customUserDetails, long postId) {
        CommunityPost communityPost = communityPostRepository.findByPostId(postId)
            .orElseThrow(() -> new StatusCode400Exception(ErrorCase._400_NO_SUCH_POST));
        Member member = memberRepository.findByMemberId(communityPost.getMember().getMemberId()).orElseGet(() -> null);

        PostDetailsResponse response = PostDetailsResponse.of(communityPost, member);
        if (customUserDetails != null) {
            Member requester = customUserDetails.getMember();
            PostExpression postExpression = postExpressionRepository.findByMemberAndPost(communityPost, requester)
                .orElseGet(() -> null);
            response.setRequesterInfo(
                RequesterInfo.builder()
                    .scrap(scrapRepository.findByMemberAndPost(communityPost, requester).isPresent())
                    .like(postExpression != null && postExpression.getExpressionType().equals(ExpressionType.LIKE))
                    .hate(postExpression != null && postExpression.getExpressionType().equals(ExpressionType.HATE))
                    .build()
            );
        }
        communityPost.increaseViews();

        return response;
    }
}
