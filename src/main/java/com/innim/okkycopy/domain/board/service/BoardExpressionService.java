package com.innim.okkycopy.domain.board.service;

import com.innim.okkycopy.domain.board.entity.Post;
import com.innim.okkycopy.domain.board.entity.PostExpression;
import com.innim.okkycopy.domain.board.enums.ExpressionType;
import com.innim.okkycopy.domain.board.repository.PostExpressionRepository;
import com.innim.okkycopy.domain.board.repository.PostRepository;
import com.innim.okkycopy.domain.member.entity.Member;
import com.innim.okkycopy.global.error.ErrorCase;
import com.innim.okkycopy.global.error.exception.StatusCode400Exception;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BoardExpressionService {

    private final PostRepository postRepository;
    private final PostExpressionRepository postExpressionRepository;

    @PersistenceContext
    private EntityManager entityManager;


    @Transactional
    public void addPostExpression(Member member, long postId, ExpressionType type) {
        Member mergedMember = entityManager.merge(member);
        Post post = postRepository.findByPostId(postId)
            .orElseThrow(() -> new StatusCode400Exception(ErrorCase._400_NO_SUCH_POST));
        if (postExpressionRepository.findByMemberAndPost(post, mergedMember).isPresent()) {
            throw new StatusCode400Exception(ErrorCase._400_ALREADY_EXIST_EXPRESSION);
        }
        PostExpression postExpression = PostExpression.of(post, mergedMember, type);
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
