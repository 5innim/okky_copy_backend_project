package com.innim.okkycopy.domain.board.comment.service;

import com.innim.okkycopy.domain.board.comment.entity.Comment;
import com.innim.okkycopy.domain.board.comment.entity.CommentExpression;
import com.innim.okkycopy.domain.board.comment.repository.CommentExpressionRepository;
import com.innim.okkycopy.domain.board.comment.repository.CommentRepository;
import com.innim.okkycopy.domain.board.enums.ExpressionType;
import com.innim.okkycopy.domain.member.entity.Member;
import com.innim.okkycopy.domain.member.repository.MemberRepository;
import com.innim.okkycopy.global.error.ErrorCase;
import com.innim.okkycopy.global.error.exception.StatusCode400Exception;
import com.innim.okkycopy.global.error.exception.StatusCode401Exception;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentExpressionService {

    private final CommentRepository commentRepository;
    private final CommentExpressionRepository commentExpressionRepository;
    private final MemberRepository memberRepository;

    @PersistenceContext
    private final EntityManager entityManager;

    @Transactional
    public void addCommentExpression(Member requester, long commentId, ExpressionType type) {
        Member member = memberRepository.findByMemberId(requester.getMemberId()).orElseThrow(
            () -> new StatusCode401Exception(ErrorCase._401_NO_SUCH_MEMBER)
        );
        Comment comment = commentRepository
            .findByCommentId(commentId)
            .orElseThrow(() -> new StatusCode400Exception(ErrorCase._400_NO_SUCH_COMMENT));
        if (commentExpressionRepository.findByMemberAndComment(comment, member).isPresent()) {
            throw new StatusCode400Exception(ErrorCase._400_ALREADY_EXIST_EXPRESSION);
        }
        if (CommentExpression.isNotSupportedCase(comment)) {
            throw new StatusCode400Exception(ErrorCase._400_NOT_SUPPORTED_CASE);
        }

        commentExpressionRepository.save(CommentExpression.of(comment, member, type));
    }

    @Transactional
    public void removeCommentExpression(Member requester, long commentId, ExpressionType type) {
        Member member = memberRepository.findByMemberId(requester.getMemberId()).orElseThrow(
            () -> new StatusCode401Exception(ErrorCase._401_NO_SUCH_MEMBER)
        );
        Comment comment = commentRepository.findByCommentId(commentId)
            .orElseThrow(() -> new StatusCode400Exception(ErrorCase._400_NO_SUCH_COMMENT));
        CommentExpression commentExpression = commentExpressionRepository.findByMemberAndComment(comment, member)
            .orElseGet(() -> null);
        if (commentExpression == null || !commentExpression.getExpressionType().equals(type)) {
            throw new StatusCode400Exception(ErrorCase._400_NOT_REGISTERED_BEFORE);
        }

        CommentExpression.remove(entityManager, commentExpression, comment, type);
    }

    @Transactional(readOnly = true)
    public List<Short> findRequesterExpressionType(List<Long> commentIds, Member requester, int batchSize) {
        List<Short> expressionTypes = new ArrayList<>();
        if (requester != null) {
            int batchQuotient = commentIds.size() / batchSize;
            for (int i = 0; i < batchQuotient; i++) {
                expressionTypes.addAll(commentExpressionRepository.findRequesterExpressionType(
                    commentIds.subList(i * batchSize, (i + 1) * batchSize),
                    requester.getMemberId()));
            }
            if (commentIds.size() % batchSize != 0) {
                expressionTypes.addAll(commentExpressionRepository.findRequesterExpressionType(
                    commentIds.subList(commentIds.size() - commentIds.size() % batchSize, commentIds.size()),
                    requester.getMemberId()));
            }
        }
        return expressionTypes;
    }
}
