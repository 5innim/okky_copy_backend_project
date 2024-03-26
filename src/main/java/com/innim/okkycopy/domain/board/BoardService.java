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
import com.innim.okkycopy.global.error.ErrorCode;
import com.innim.okkycopy.global.error.exception.AlreadyExistExpressionException;
import com.innim.okkycopy.global.error.exception.FailInitializationException;
import com.innim.okkycopy.global.error.exception.NoSuchPostException;
import com.innim.okkycopy.global.error.exception.NoSuchScrapException;
import com.innim.okkycopy.global.error.exception.NotRegisteredBeforeException;
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
    public TopicsResponse findAllBoardTopics() {
        List<BoardType> boardTypes = boardTypeRepository.findAll();

        if (boardTypes.isEmpty()) {
            throw new FailInitializationException(ErrorCode._500_FAIL_INITIALIZATION);
        }

        return TopicsResponse.toDto(boardTypes);
    }

    @Transactional
    public void scrapPost(Member member, long postId) {
        Member mergedMember = entityManager.merge(member);
        Post post = postRepository.findByPostId(postId)
            .orElseThrow(() -> new NoSuchPostException(ErrorCode._400_NO_SUCH_POST));
        entityManager.persist(Scrap.createScrap(post, mergedMember));
    }

    @Transactional
    public void cancelScrap(Member member, long postId) {
        Member mergedMember = entityManager.merge(member);
        Post post = postRepository.findByPostId(postId)
            .orElseThrow(() -> new NoSuchPostException(ErrorCode._400_NO_SUCH_POST));
        Scrap scrap = scrapRepository.findByMemberAndPost(post, mergedMember)
            .orElseThrow(() -> new NoSuchScrapException(ErrorCode._400_NO_SUCH_SCRAP));
        Scrap.removeScrap(entityManager, scrap);
    }

    @Transactional
    public void insertPostExpression(Member member, long postId, ExpressionType type) {
        Member mergedMember = entityManager.merge(member);
        Post post = postRepository.findByPostId(postId)
            .orElseThrow(() -> new NoSuchPostException(ErrorCode._400_NO_SUCH_POST));
        if (postExpressionRepository.findByMemberAndPost(post, mergedMember).isPresent()) {
            throw new AlreadyExistExpressionException(ErrorCode._400_ALREADY_EXIST_EXPRESSION);
        }
        PostExpression postExpression = PostExpression.createPostExpression(post, mergedMember, type);
        entityManager.persist(postExpression);
    }

    @Transactional
    public void deletePostExpression(Member member, long postId, ExpressionType type) {
        Member mergedMember = entityManager.merge(member);
        Post post = postRepository.findByPostId(postId)
            .orElseThrow(() -> new NoSuchPostException(ErrorCode._400_NO_SUCH_POST));
        PostExpression postExpression = postExpressionRepository.findByMemberAndPost(post, mergedMember)
            .orElseGet(() -> null);
        if (postExpression == null || !postExpression.getExpressionType().equals(type)) {
            throw new NotRegisteredBeforeException(ErrorCode._400_NOT_REGISTERED_BEFORE);
        }
        PostExpression.removePostExpression(entityManager, postExpression, post, type);
    }

}
