package com.innim.okkycopy.domain.board.community;

import com.innim.okkycopy.domain.board.community.entity.CommunityPost;
import com.innim.okkycopy.domain.board.dto.request.write.PostRequest;
import com.innim.okkycopy.domain.board.entity.BoardTopic;
import com.innim.okkycopy.domain.board.repository.BoardTopicRepository;
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
}
