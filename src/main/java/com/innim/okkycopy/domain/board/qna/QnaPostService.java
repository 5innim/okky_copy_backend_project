package com.innim.okkycopy.domain.board.qna;

import com.innim.okkycopy.domain.board.dto.request.write.PostRequest;
import com.innim.okkycopy.domain.board.dto.response.post.brief.PostListResponse;
import com.innim.okkycopy.domain.board.dto.response.post.detail.PostDetailsResponse;
import com.innim.okkycopy.domain.board.dto.response.post.detail.RequesterInfo;
import com.innim.okkycopy.domain.board.entity.BoardTopic;
import com.innim.okkycopy.domain.board.entity.PostExpression;
import com.innim.okkycopy.domain.board.enums.ExpressionType;
import com.innim.okkycopy.domain.board.qna.entity.QnaPost;
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
public class QnaPostService {

    private final BoardTopicRepository boardTopicRepository;
    private final QnaPostRepository qnaPostRepository;
    private final MemberRepository memberRepository;
    private final PostExpressionRepository postExpressionRepository;
    private final ScrapRepository scrapRepository;
    private final ImageUsageService imageUsageService;

    @PersistenceContext
    private final EntityManager entityManager;

    @Transactional
    public void addQnaPost(PostRequest postRequest, CustomUserDetails customUserDetails) {
        Member member = memberRepository.findByMemberId(customUserDetails.getUserId()).orElseThrow(
            () -> new StatusCode401Exception(ErrorCase._401_NO_SUCH_MEMBER)
        );

        BoardTopic boardTopic = boardTopicRepository.findByName(postRequest.getTopic())
            .orElseThrow(() -> new StatusCode400Exception(
                ErrorCase._400_NO_SUCH_TOPIC));

        imageUsageService.modifyImageUsages(postRequest.getContent());
        QnaPost qnaPost = QnaPost.of(postRequest, boardTopic, member);
        qnaPostRepository.save(qnaPost);
    }

    @Transactional
    public PostDetailsResponse findQnaPost(CustomUserDetails customUserDetails, long postId) {
        QnaPost qnaPost = qnaPostRepository.findByPostId(postId)
            .orElseThrow(() -> new StatusCode400Exception(ErrorCase._400_NO_SUCH_POST));

        PostDetailsResponse response = PostDetailsResponse.from(qnaPost);
        if (customUserDetails != null) {
            Member requester = customUserDetails.getMember();
            PostExpression postExpression = postExpressionRepository.findByMemberAndPost(qnaPost, requester)
                .orElseGet(() -> null);
            response.setRequesterInfo(
                RequesterInfo.builder()
                    .scrap(scrapRepository.findByMemberAndPost(qnaPost, requester).isPresent())
                    .like(postExpression != null && postExpression.getExpressionType().equals(ExpressionType.LIKE))
                    .hate(postExpression != null && postExpression.getExpressionType().equals(ExpressionType.HATE))
                    .build()
            );
        }
        qnaPost.increaseViews();

        return response;
    }

    @Transactional
    public void modifyQnaPost(CustomUserDetails customUserDetails, PostRequest updateRequest, long postId) {
        Member member = memberRepository.findByMemberId(customUserDetails.getUserId()).orElseThrow(
            () -> new StatusCode401Exception(ErrorCase._401_NO_SUCH_MEMBER)
        );
        QnaPost qnaPost = qnaPostRepository.findByPostId(postId)
            .orElseThrow(() -> new StatusCode400Exception(ErrorCase._400_NO_SUCH_POST));
        BoardTopic boardTopic = boardTopicRepository.findByName(updateRequest.getTopic())
            .orElseThrow(() -> new StatusCode400Exception(
                ErrorCase._400_NO_SUCH_TOPIC));

        if (qnaPost.getMember() == null || qnaPost.getMember().getMemberId() != member.getMemberId()) {
            throw new StatusCode403Exception(ErrorCase._403_NO_AUTHORITY);
        }
        imageUsageService.modifyImageUsages(qnaPost.getContent(), updateRequest.getContent());
        qnaPost.update(updateRequest, boardTopic);
    }

    @Transactional
    public void removeQnaPost(CustomUserDetails customUserDetails, long postId) {
        Member member = memberRepository.findByMemberId(customUserDetails.getUserId()).orElseThrow(
            () -> new StatusCode401Exception(ErrorCase._401_NO_SUCH_MEMBER)
        );
        QnaPost qnaPost = qnaPostRepository.findByPostId(postId)
            .orElseThrow(() -> new StatusCode400Exception(ErrorCase._400_NO_SUCH_POST));

        if (qnaPost.getMember() == null || qnaPost.getMember().getMemberId() != member.getMemberId()) {
            throw new StatusCode403Exception(ErrorCase._403_NO_AUTHORITY);
        }

        qnaPost.remove(entityManager);
    }

    @Transactional(readOnly = true)
    public PostListResponse findQnaPostsByTopicIdAndKeyword(Long topicId, String keyword, Pageable pageable) {
        Page<QnaPost> qnaPostPage;
        if (topicId == null) {
            qnaPostPage = qnaPostRepository.findPageByKeyword((keyword == null) ? "" : keyword,
                pageable);
        } else {
            BoardTopic boardTopic = boardTopicRepository
                .findByTopicId(topicId)
                .orElseThrow(() -> new StatusCode400Exception(ErrorCase._400_NO_SUCH_TOPIC));
            if (QnaPost.isNotSupportedTopic(boardTopic)) {
                throw new StatusCode400Exception(ErrorCase._400_NOT_SUPPORTED_CASE);
            }
            qnaPostPage = qnaPostRepository.findPageByBoardTopicAndKeyword(boardTopic, (keyword == null) ? "" : keyword,
                pageable);
        }

        return PostListResponse.from(qnaPostPage);
    }

}
