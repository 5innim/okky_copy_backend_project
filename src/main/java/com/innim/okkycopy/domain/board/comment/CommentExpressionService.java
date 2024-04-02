package com.innim.okkycopy.domain.board.comment;

import com.innim.okkycopy.domain.board.comment.entity.Comment;
import com.innim.okkycopy.domain.board.comment.entity.CommentExpression;
import com.innim.okkycopy.domain.board.comment.repository.CommentExpressionRepository;
import com.innim.okkycopy.domain.board.comment.repository.CommentRepository;
import com.innim.okkycopy.domain.board.enums.ExpressionType;
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
public class CommentExpressionService {

    private final CommentRepository commentRepository;
    private final CommentExpressionRepository commentExpressionRepository;

    @PersistenceContext
    private final EntityManager entityManager;

    @Transactional
    public void addCommentExpression(Member member, long commentId, ExpressionType type) {
        Member mergedMember = entityManager.merge(member);
        Comment comment = commentRepository
            .findByCommentId(commentId)
            .orElseThrow(() -> new StatusCode400Exception(ErrorCase._400_NO_SUCH_COMMENT));
        if (commentExpressionRepository.findByMemberAndComment(comment, mergedMember).isPresent()) {
            throw new StatusCode400Exception(ErrorCase._400_ALREADY_EXIST_EXPRESSION);
        }
        if (CommentExpression.isNotSupportedCase(comment)) {
            throw new StatusCode400Exception(ErrorCase._400_NOT_SUPPORTED_CASE);
        }
        entityManager.persist(CommentExpression.of(comment, mergedMember, type));
    }

    @Transactional
    public void removeCommentExpression(Member member, long commentId, ExpressionType type) {
        Member mergedMember = entityManager.merge(member);
        Comment comment = commentRepository.findByCommentId(commentId)
            .orElseThrow(() -> new StatusCode400Exception(ErrorCase._400_NO_SUCH_COMMENT));
        CommentExpression commentExpression = commentExpressionRepository.findByMemberAndComment(comment, mergedMember)
            .orElseGet(() -> null);
        if (commentExpression == null || !commentExpression.getExpressionType().equals(type)) {
            throw new StatusCode400Exception(ErrorCase._400_NOT_REGISTERED_BEFORE);
        }
        CommentExpression.remove(entityManager, commentExpression, comment, type);
    }
}
