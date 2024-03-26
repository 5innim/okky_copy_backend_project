package com.innim.okkycopy.domain.board;

import com.innim.okkycopy.domain.board.dto.response.topics.TopicsResponse;
import com.innim.okkycopy.domain.board.entity.BoardType;
import com.innim.okkycopy.domain.board.entity.Post;
import com.innim.okkycopy.domain.board.entity.PostExpression;
import com.innim.okkycopy.domain.board.entity.Scrap;
import com.innim.okkycopy.domain.board.enums.ExpressionType;
import com.innim.okkycopy.domain.board.repository.BoardTypeRepository;
import com.innim.okkycopy.domain.board.repository.PostExpressionRepository;
import com.innim.okkycopy.domain.board.repository.PostRepository;
import com.innim.okkycopy.domain.board.repository.ScrapRepository;
import com.innim.okkycopy.domain.member.entity.Member;
import com.innim.okkycopy.global.error.ErrorCase;
import com.innim.okkycopy.global.error.exception.StatusCode400Exception;
import com.innim.okkycopy.global.error.exception.StatusCode500Exception;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardTypeRepository boardTypeRepository;
    private final PostRepository postRepository;
    private final ScrapRepository scrapRepository;
    private final PostExpressionRepository postExpressionRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional(readOnly = true)
    public TopicsResponse findBoardTopics() {
        List<BoardType> boardTypes = boardTypeRepository.findAll();

        if (boardTypes.isEmpty()) {
            throw new StatusCode500Exception(ErrorCase._500_FAIL_INITIALIZATION);
        }

        return TopicsResponse.toDto(boardTypes);
    }

    @Transactional
    public void addScrap(Member member, long postId) {
        Member mergedMember = entityManager.merge(member);
        Post post = postRepository.findByPostId(postId)
            .orElseThrow(() -> new StatusCode400Exception(ErrorCase._400_NO_SUCH_POST));
        entityManager.persist(Scrap.create(post, mergedMember));
    }

    @Transactional
    public void removeScrap(Member member, long postId) {
        Member mergedMember = entityManager.merge(member);
        Post post = postRepository.findByPostId(postId)
            .orElseThrow(() -> new StatusCode400Exception(ErrorCase._400_NO_SUCH_POST));
        Scrap scrap = scrapRepository.findByMemberAndPost(post, mergedMember)
            .orElseThrow(() -> new StatusCode400Exception(ErrorCase._400_NO_SUCH_SCRAP));
        Scrap.remove(entityManager, scrap);
    }

    @Transactional
    public void addPostExpression(Member member, long postId, ExpressionType type) {
        Member mergedMember = entityManager.merge(member);
        Post post = postRepository.findByPostId(postId)
            .orElseThrow(() -> new StatusCode400Exception(ErrorCase._400_NO_SUCH_POST));
        if (postExpressionRepository.findByMemberAndPost(post, mergedMember).isPresent()) {
            throw new StatusCode400Exception(ErrorCase._400_ALREADY_EXIST_EXPRESSION);
        }
        PostExpression postExpression = PostExpression.create(post, mergedMember, type);
        entityManager.persist(postExpression);
    }

    @Transactional
    public void removePostExpression(Member member, long postId, ExpressionType type) {
        Member mergedMember = entityManager.merge(member);
        Post post = postRepository.findByPostId(postId)
            .orElseThrow(() -> new StatusCode400Exception(ErrorCase._400_NO_SUCH_POST));
        PostExpression postExpression = postExpressionRepository.findByMemberAndPost(post, mergedMember)
            .orElseGet(() -> null);
        if (postExpression == null || !postExpression.getExpressionType().equals(type)) {
            throw new StatusCode400Exception(ErrorCase._400_NOT_REGISTERED_BEFORE);
        }
        PostExpression.remove(entityManager, postExpression, post, type);
    }

}
