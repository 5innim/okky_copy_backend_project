package com.innim.okkycopy.domain.board.event;

import com.innim.okkycopy.domain.board.dto.request.write.PostRequest;
import com.innim.okkycopy.domain.board.dto.response.post.PostDetailsResponse;
import com.innim.okkycopy.domain.board.dto.response.post.PostListResponse;
import com.innim.okkycopy.domain.board.entity.BoardTopic;
import com.innim.okkycopy.domain.board.entity.PostExpression;
import com.innim.okkycopy.domain.board.event.entity.EventPost;
import com.innim.okkycopy.domain.board.event.interfaces.EventPostActionable;
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
public class EventPostService {

    private final BoardTopicRepository boardTopicRepository;
    private final EventPostRepository eventPostRepository;
    private final MemberRepository memberRepository;
    private final PostExpressionRepository postExpressionRepository;
    private final ScrapRepository scrapRepository;
    private final ImageUsageService imageUsageService;

    @PersistenceContext
    private final EntityManager entityManager;

    @Transactional
    public void addEventPost(PostRequest postRequest, CustomUserDetails customUserDetails) {
        Member member = memberRepository.findByMemberId(customUserDetails.getUserId()).orElseThrow(
            () -> new StatusCode401Exception(ErrorCase._401_NO_SUCH_MEMBER)
        );

        BoardTopic boardTopic = boardTopicRepository.findByName(postRequest.getTopic())
            .orElseThrow(() -> new StatusCode400Exception(
                ErrorCase._400_NO_SUCH_TOPIC));

        imageUsageService.modifyImageUsages(postRequest.getContent(), true);
        EventPost eventPost = EventPost.of(postRequest, boardTopic, member);

        eventPostRepository.save(eventPost);
    }

    @Transactional
    public PostDetailsResponse findEventPost(CustomUserDetails customUserDetails, long postId) {
        EventPost eventPost = eventPostRepository.findByPostId(postId)
            .orElseThrow(() -> new StatusCode400Exception(ErrorCase._400_NO_SUCH_POST));

        PostDetailsResponse response;
        if (customUserDetails != null) {
            Member requester = customUserDetails.getMember();
            PostExpression postExpression = postExpressionRepository.findByMemberAndPost(eventPost, requester)
                .orElseGet(() -> null);
            response = PostDetailsResponse.from(eventPost, postExpression,
                scrapRepository.findByMemberAndPost(eventPost, requester).isPresent());
        } else {
            response = PostDetailsResponse.from(eventPost);
        }
        eventPost.increaseViews();

        return response;
    }

    @Transactional
    public void modifyEventPost(CustomUserDetails customUserDetails, PostRequest updateRequest, long postId) {
        Member member = memberRepository.findByMemberId(customUserDetails.getUserId()).orElseThrow(
            () -> new StatusCode401Exception(ErrorCase._401_NO_SUCH_MEMBER)
        );
        EventPost eventPost = eventPostRepository.findByPostId(postId)
            .orElseThrow(() -> new StatusCode400Exception(ErrorCase._400_NO_SUCH_POST));
        BoardTopic boardTopic = boardTopicRepository.findByName(updateRequest.getTopic())
            .orElseThrow(() -> new StatusCode400Exception(
                ErrorCase._400_NO_SUCH_TOPIC));

        if (eventPost.getMember() == null || eventPost.getMember().getMemberId() != member.getMemberId()) {
            throw new StatusCode403Exception(ErrorCase._403_NO_AUTHORITY);
        }
        imageUsageService.modifyImageUsages(eventPost.getContent(), updateRequest.getContent());

        ((EventPostActionable) eventPost).update(updateRequest, boardTopic);
    }

    @Transactional
    public void removeEventPost(CustomUserDetails customUserDetails, long postId) {
        Member member = memberRepository.findByMemberId(customUserDetails.getUserId()).orElseThrow(
            () -> new StatusCode401Exception(ErrorCase._401_NO_SUCH_MEMBER)
        );
        EventPost eventPost = eventPostRepository.findByPostId(postId)
            .orElseThrow(() -> new StatusCode400Exception(ErrorCase._400_NO_SUCH_POST));

        if (eventPost.getMember() == null || eventPost.getMember().getMemberId() != member.getMemberId()) {
            throw new StatusCode403Exception(ErrorCase._403_NO_AUTHORITY);
        }
        imageUsageService.modifyImageUsages(eventPost.getContent(), false);
        eventPost.remove(entityManager);
    }

    @Transactional(readOnly = true)
    public PostListResponse findEventPostsByTopicIdAndKeyword(Long topicId, String keyword, Pageable pageable) {
        Page<EventPost> eventPostPage;
        if (topicId == null) {
            eventPostPage = eventPostRepository.findPageByKeyword((keyword == null) ? "" : keyword,
                pageable);
        } else {
            BoardTopic boardTopic = boardTopicRepository
                .findByTopicId(topicId)
                .orElseThrow(() -> new StatusCode400Exception(ErrorCase._400_NO_SUCH_TOPIC));
            if (EventPost.isNotSupportedTopic(boardTopic)) {
                throw new StatusCode400Exception(ErrorCase._400_NOT_SUPPORTED_CASE);
            }
            eventPostPage = eventPostRepository
                .findPageByBoardTopicAndKeyword(boardTopic, (keyword == null) ? "" : keyword, pageable);
        }

        return PostListResponse.from(eventPostPage);
    }


}
