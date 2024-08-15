package com.innim.okkycopy.domain.board.comment.service;

import com.innim.okkycopy.domain.board.comment.dto.request.CommentRequest;
import com.innim.okkycopy.domain.board.comment.dto.request.ReCommentRequest;
import com.innim.okkycopy.domain.board.comment.dto.response.CommentDetailsResponse;
import com.innim.okkycopy.domain.board.comment.dto.response.CommentListResponse;
import com.innim.okkycopy.domain.board.comment.dto.response.ReCommentDetailsResponse;
import com.innim.okkycopy.domain.board.comment.dto.response.ReCommentListResponse;
import com.innim.okkycopy.domain.board.comment.dto.response.RequesterInfo;
import com.innim.okkycopy.domain.board.comment.entity.Comment;
import com.innim.okkycopy.domain.board.comment.repository.CommentExpressionRepository;
import com.innim.okkycopy.domain.board.comment.repository.CommentRepository;
import com.innim.okkycopy.domain.board.entity.Post;
import com.innim.okkycopy.domain.board.enums.ExpressionType;
import com.innim.okkycopy.domain.board.repository.PostRepository;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentCrudService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;
    private final CommentExpressionRepository commentExpressionRepository;
    private final ImageUsageService imageUsageService;

    @PersistenceContext
    private final EntityManager entityManager;

    @Transactional
    public void addComment(CustomUserDetails customUserDetails,
        CommentRequest commentRequest,
        long postId) {
        Member member = memberRepository.findByMemberId(customUserDetails.getUserId()).orElseThrow(
            () -> new StatusCode401Exception(ErrorCase._401_NO_SUCH_MEMBER)
        );

        Post post = postRepository.findByPostId(postId)
            .orElseThrow(() -> new StatusCode400Exception(ErrorCase._400_NO_SUCH_POST));

        imageUsageService.modifyImageUsages(commentRequest.getContent(), true);
        Comment comment = Comment.of(post, member,
            commentRequest);
        commentRepository.save(comment);
    }

    @Transactional
    public void modifyComment(CustomUserDetails customUserDetails,
        CommentRequest commentRequest,
        long commentId) {
        Member member = memberRepository.findByMemberId(customUserDetails.getUserId()).orElseThrow(
            () -> new StatusCode401Exception(ErrorCase._401_NO_SUCH_MEMBER)
        );
        Comment comment = commentRepository.findByCommentId(commentId)
            .orElseThrow(() -> new StatusCode400Exception(ErrorCase._400_NO_SUCH_COMMENT));

        if (comment.getMember() == null || comment.getMember().getMemberId() != member.getMemberId()) {
            throw new StatusCode403Exception(ErrorCase._403_NO_AUTHORITY);
        }

        imageUsageService.modifyImageUsages(comment.getContent(), commentRequest.getContent());
        comment.update(commentRequest.getContent());
    }

    @Transactional
    public void removeComment(CustomUserDetails customUserDetails, long commentId) {
        Member member = memberRepository.findByMemberId(customUserDetails.getUserId()).orElseThrow(
            () -> new StatusCode401Exception(ErrorCase._401_NO_SUCH_MEMBER)
        );
        Comment comment = commentRepository.findByCommentId(commentId)
            .orElseThrow(() -> new StatusCode400Exception(ErrorCase._400_NO_SUCH_COMMENT));

        if (comment.getMember() == null || comment.getMember().getMemberId() != member.getMemberId()) {
            throw new StatusCode403Exception(ErrorCase._403_NO_AUTHORITY);
        }
        List<Comment> commentList = commentRepository.findByParentIdOrderByCreatedDateAsc(comment.getCommentId());
        for (Comment c : commentList) {
            imageUsageService.modifyImageUsages(c.getContent(), false);
            Comment.remove(c, entityManager);
        }

        imageUsageService.modifyImageUsages(comment.getContent(), false);
        Comment.remove(comment, entityManager);

    }

    @Transactional(readOnly = true)
    public CommentListResponse findComments(CustomUserDetails customUserDetails, long postId) {
        Post post = postRepository.findByPostId(postId)
            .orElseThrow(() -> new StatusCode400Exception(ErrorCase._400_NO_SUCH_POST));

        List<Comment> parentComments = post.getCommentList().stream().filter(comment -> comment.getDepth() == 1)
            .toList();

        Member requester = (customUserDetails == null) ? null : customUserDetails.getMember();
        List<CommentDetailsResponse> commentResponses = new ArrayList<>();
        List<Short> expressionTypes = findRequesterExpressionType(
            parentComments.stream().map(Comment::getCommentId).toList(), requester, 20);

        for (int i = 0; i < parentComments.size(); i++) {
            RequesterInfo requesterInfo =
                (requester == null) ? null : RequesterInfo.builder()
                    .like(
                        expressionTypes.get(i) != null && Objects.equals(Integer.valueOf(expressionTypes.get(i)),
                            ExpressionType.LIKE.getValue()))
                    .hate(
                        expressionTypes.get(i) != null && Objects.equals(Integer.valueOf(expressionTypes.get(i)),
                            ExpressionType.HATE.getValue()))
                    .build();

            commentResponses.add(
                CommentDetailsResponse.of(
                    parentComments.get(i),
                    requesterInfo
                )
            );
        }

        return new CommentListResponse(commentResponses);
    }

    @Transactional(readOnly = true)
    public ReCommentListResponse findReComments(CustomUserDetails customUserDetails, long commentId) {
        commentRepository.findByCommentId(commentId)
            .orElseThrow(() -> new StatusCode400Exception(ErrorCase._400_NO_SUCH_COMMENT));

        List<Comment> comments = commentRepository.findByParentIdOrderByCreatedDateAsc(commentId);
        List<ReCommentDetailsResponse> commentResponses = new ArrayList<>();

        Member requester = (customUserDetails == null) ? null : customUserDetails.getMember();

        List<Long> commentIds = comments.stream().map(Comment::getCommentId).toList();

        List<Short> expressionTypes = findRequesterExpressionType(commentIds, requester, 20);
        List<String> mentionedNicknames = findMentionedNickname(commentIds, 20);

        for (int i = 0; i < comments.size(); i++) {
            RequesterInfo requesterInfo =
                (requester == null) ? null : RequesterInfo.builder()
                    .like(
                        expressionTypes.get(i) != null && Objects.equals(Integer.valueOf(expressionTypes.get(i)),
                            ExpressionType.LIKE.getValue()))
                    .hate(
                        expressionTypes.get(i) != null && Objects.equals(Integer.valueOf(expressionTypes.get(i)),
                            ExpressionType.HATE.getValue()))
                    .build();

            commentResponses.add(
                ReCommentDetailsResponse.of(comments.get(i), mentionedNicknames.get(i), requesterInfo)
            );
        }

        return new ReCommentListResponse(commentResponses);
    }

    @Transactional(readOnly = true)
    List<Short> findRequesterExpressionType(List<Long> commentIds, Member requester, int batchSize) {
        List<Short> expressionTypes = new ArrayList<>();
        if (requester != null) {
            int batchQuotient = commentIds.size() / batchSize;
            for (int i = 0; i < batchQuotient; i++) {
                expressionTypes.addAll(commentExpressionRepository.findRequesterExpression(
                    commentIds.subList(i * batchSize, (i + 1) * batchSize),
                    requester.getMemberId()));
            }
            if (commentIds.size() % batchSize != 0) {
                expressionTypes.addAll(commentExpressionRepository.findRequesterExpression(
                    commentIds.subList(commentIds.size() - commentIds.size() % batchSize, commentIds.size()),
                    requester.getMemberId()));
            }
        }
        return expressionTypes;
    }

    @Transactional(readOnly = true)
    List<String> findMentionedNickname(List<Long> commentIds, int batchSize) {
        List<String> mentionedNicknames = new ArrayList<>();

        int batchQuotient = commentIds.size() / batchSize;
        for (int i = 0; i < batchQuotient; i++) {
            mentionedNicknames.addAll(commentRepository.findMentionedNickname(
                commentIds.subList(i * batchSize, (i + 1) * batchSize)));
        }
        if (commentIds.size() % batchSize != 0) {
            mentionedNicknames.addAll(commentRepository.findMentionedNickname(
                commentIds.subList(commentIds.size() - commentIds.size() % batchSize, commentIds.size())));
        }
        return mentionedNicknames;
    }


    @Transactional
    public void addReComment(
        CustomUserDetails customUserDetails,
        long commentId,
        ReCommentRequest reCommentRequest) {
        Member member = memberRepository.findByMemberId(customUserDetails.getUserId()).orElseThrow(
            () -> new StatusCode401Exception(ErrorCase._401_NO_SUCH_MEMBER)
        );

        Comment comment = commentRepository.findByCommentId(commentId)
            .orElseThrow(() -> new StatusCode400Exception(ErrorCase._400_NO_SUCH_COMMENT));

        if (comment.getDepth() > 1) {
            throw new StatusCode400Exception(ErrorCase._400_NOT_SUPPORTED_CASE);
        }

        Comment reComment = Comment.reCommentOf(comment.getPost(), member,
            commentId, reCommentRequest);

        commentRepository.save(reComment);
    }


}
