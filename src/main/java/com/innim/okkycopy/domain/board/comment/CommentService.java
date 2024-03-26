package com.innim.okkycopy.domain.board.comment;

import com.innim.okkycopy.domain.board.comment.dto.request.CommentRequest;
import com.innim.okkycopy.domain.board.comment.dto.request.ReCommentRequest;
import com.innim.okkycopy.domain.board.comment.dto.response.CommentDetailsResponse;
import com.innim.okkycopy.domain.board.comment.dto.response.CommentListResponse;
import com.innim.okkycopy.domain.board.comment.dto.response.RequesterInfo;
import com.innim.okkycopy.domain.board.comment.entity.Comment;
import com.innim.okkycopy.domain.board.comment.entity.CommentExpression;
import com.innim.okkycopy.domain.board.comment.repository.CommentExpressionRepository;
import com.innim.okkycopy.domain.board.comment.repository.CommentRepository;
import com.innim.okkycopy.domain.board.entity.Post;
import com.innim.okkycopy.domain.board.enums.ExpressionType;
import com.innim.okkycopy.domain.board.repository.PostRepository;
import com.innim.okkycopy.domain.member.MemberRepository;
import com.innim.okkycopy.domain.member.entity.Member;
import com.innim.okkycopy.global.auth.CustomUserDetails;
import com.innim.okkycopy.global.error.ErrorCase;
import com.innim.okkycopy.global.error.exception.StatusCode400Exception;
import com.innim.okkycopy.global.error.exception.StatusCode403Exception;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;
    private final CommentExpressionRepository commentExpressionRepository;

    @PersistenceContext
    private final EntityManager entityManager;

    @Transactional
    public void addComment(CustomUserDetails customUserDetails,
        CommentRequest commentRequest,
        long postId) {
        Member mergedMember = entityManager.merge(customUserDetails.getMember());
        Post post = postRepository.findByPostId(postId)
            .orElseThrow(() -> new StatusCode400Exception(ErrorCase._400_NO_SUCH_POST));

        Comment comment = Comment.create(post, mergedMember,
            commentRequest);
        entityManager.persist(comment);
    }

    @Transactional
    public void modifyComment(CustomUserDetails customUserDetails,
        CommentRequest commentRequest,
        long commentId) {
        Member mergedMember = entityManager.merge(customUserDetails.getMember());
        Comment comment = commentRepository.findByCommentId(commentId)
            .orElseThrow(() -> new StatusCode400Exception(ErrorCase._400_NO_SUCH_COMMENT));

        if (comment.getMember().getMemberId() != mergedMember.getMemberId()) {
            throw new StatusCode403Exception(ErrorCase._403_NO_AUTHORITY);
        }
        comment.update(commentRequest.getContent());
    }

    @Transactional
    public void removeComment(CustomUserDetails customUserDetails, long commentId) {
        Member mergedMember = entityManager.merge(customUserDetails.getMember());
        Comment comment = commentRepository.findByCommentId(commentId)
            .orElseThrow(() -> new StatusCode400Exception(ErrorCase._400_NO_SUCH_COMMENT));

        if (comment.getMember().getMemberId() != mergedMember.getMemberId()) {
            throw new StatusCode403Exception(ErrorCase._403_NO_AUTHORITY);
        }
        List<Comment> commentList = commentRepository.findByParentId(comment.getCommentId());
        for (Comment c : commentList) {
            Comment.remove(c, entityManager);
        }
        Comment.remove(comment, entityManager);

    }

    @Transactional(readOnly = true)
    public CommentListResponse findComments(CustomUserDetails customUserDetails, long postId) {
        Post post = postRepository.findByPostId(postId)
            .orElseThrow(() -> new StatusCode400Exception(ErrorCase._400_NO_SUCH_POST));
        List<Comment> parentComments = post.getCommentList().stream()
            .filter(comment -> (comment.getParentId() == null))
            .toList();

        List<CommentDetailsResponse> commentResponses = new ArrayList<>();
        Member requester = (customUserDetails == null) ? null : customUserDetails.getMember();
        for (Comment comment : parentComments) {
            CommentExpression commentExpression = (requester == null) ? null : commentExpressionRepository
                .findByMemberAndComment(comment, requester)
                .orElseGet(() -> null);
            RequesterInfo requesterInfo =
                (requester == null) ? null : RequesterInfo.builder()
                    .like(
                        commentExpression != null && commentExpression.getExpressionType().equals(ExpressionType.LIKE))
                    .hate(
                        commentExpression != null && commentExpression.getExpressionType().equals(ExpressionType.HATE))
                    .build();

            commentResponses.add(
                CommentDetailsResponse.toCommentResponseDto(
                    comment,
                    null,
                    requesterInfo
                )
            );
        }
        Collections.sort(commentResponses);

        return new CommentListResponse(commentResponses);
    }

    @Transactional
    public void addReComment(
        CustomUserDetails customUserDetails,
        long postId,
        long commentId,
        ReCommentRequest reCommentRequest) {
        Member mergedMember = entityManager.merge(customUserDetails.getMember());
        Post post = postRepository.findByPostId(postId)
            .orElseThrow(() -> new StatusCode400Exception(ErrorCase._400_NO_SUCH_POST));
        commentRepository.findByCommentId(commentId)
            .orElseThrow(() -> new StatusCode400Exception(ErrorCase._400_NO_SUCH_COMMENT));

        Comment reComment = Comment.createReComment(post, mergedMember,
            commentId, reCommentRequest);

        entityManager.persist(reComment);
    }

    @Transactional(readOnly = true)
    public CommentListResponse findReComments(CustomUserDetails customUserDetails, long commentId) {
        commentRepository.findByCommentId(commentId)
            .orElseThrow(() -> new StatusCode400Exception(ErrorCase._400_NO_SUCH_COMMENT));

        List<Comment> comments = commentRepository.findByParentId(commentId);
        List<CommentDetailsResponse> commentResponses = new ArrayList<>();

        Member requester = (customUserDetails == null) ? null : customUserDetails.getMember();
        for (Comment comment : comments) {
            String mentionedNickname = null;
            if (comment.getMentionedMember() != null) {
                Member member = memberRepository.findByMemberId(comment.getMentionedMember()).orElseGet(() -> null);
                mentionedNickname = (member == null) ? "(unknown)" : member.getNickname();
            }
            CommentExpression commentExpression = commentExpressionRepository
                .findByMemberAndComment(comment, requester)
                .orElseGet(() -> null);

            RequesterInfo requesterInfo =
                (requester == null) ? null : RequesterInfo.builder()
                    .like(
                        commentExpression != null && commentExpression.getExpressionType().equals(ExpressionType.LIKE))
                    .hate(
                        commentExpression != null && commentExpression.getExpressionType().equals(ExpressionType.HATE))
                    .build();

            commentResponses.add(
                CommentDetailsResponse.toCommentResponseDto(comment, mentionedNickname, requesterInfo)
            );
        }
        Collections.sort(commentResponses);

        return new CommentListResponse(commentResponses);
    }

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
        entityManager.persist(CommentExpression.create(comment, mergedMember, type));
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
        CommentExpression.removeCommentExpression(entityManager, commentExpression, comment, type);
    }
}
