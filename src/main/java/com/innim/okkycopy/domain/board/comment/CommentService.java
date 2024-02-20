package com.innim.okkycopy.domain.board.comment;

import com.innim.okkycopy.domain.board.comment.dto.request.WriteCommentRequest;
import com.innim.okkycopy.domain.board.comment.dto.request.WriteReCommentRequest;
import com.innim.okkycopy.domain.board.comment.dto.response.CommentResponse;
import com.innim.okkycopy.domain.board.comment.dto.response.CommentsResponse;
import com.innim.okkycopy.domain.board.comment.entity.Comment;
import com.innim.okkycopy.domain.board.entity.Post;
import com.innim.okkycopy.domain.board.repository.PostRepository;
import com.innim.okkycopy.domain.member.MemberRepository;
import com.innim.okkycopy.domain.member.entity.Member;
import com.innim.okkycopy.global.auth.CustomUserDetails;
import com.innim.okkycopy.global.error.ErrorCode;
import com.innim.okkycopy.global.error.exception.NoAuthorityException;
import com.innim.okkycopy.global.error.exception.NoSuchCommentException;
import com.innim.okkycopy.global.error.exception.NoSuchPostException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;

    @PersistenceContext
    private final EntityManager entityManager;

    @Transactional
    public void saveComment(CustomUserDetails customUserDetails,
                                     WriteCommentRequest writeCommentRequest,
                                     long postId) {
        Member mergedMember = entityManager.merge(customUserDetails.getMember());
        Post post = postRepository.findByPostId(postId).orElseThrow(() -> new NoSuchPostException(ErrorCode._400_NO_SUCH_POST));

        Comment comment = Comment.createComment(post, mergedMember,
                writeCommentRequest);
        entityManager.persist(comment);
    }

    @Transactional
    public void updateComment(CustomUserDetails customUserDetails,
                              WriteCommentRequest writeCommentRequest,
                              long commentId) {
        Member mergedMember = entityManager.merge(customUserDetails.getMember());
        Comment comment = commentRepository.findByCommentId(commentId).orElseThrow(() -> new NoSuchCommentException(ErrorCode._400_NO_SUCH_COMMENT));

        if (comment.getMember().getMemberId() != mergedMember.getMemberId()) throw new NoAuthorityException(ErrorCode._403_NO_AUTHORITY);
        comment.setContent(writeCommentRequest.getContent());
        comment.setLastUpdate(LocalDateTime.now());
    }

    @Transactional
    public void deleteComment(CustomUserDetails customUserDetails, long commentId) {
        Member mergedMember = entityManager.merge(customUserDetails.getMember());
        Comment comment = commentRepository.findByCommentId(commentId).orElseThrow(() -> new NoSuchCommentException(ErrorCode._400_NO_SUCH_COMMENT));

        if (comment.getMember().getMemberId() != mergedMember.getMemberId()) throw new NoAuthorityException(ErrorCode._403_NO_AUTHORITY);

        Comment.removeComment(comment, entityManager);
    }

    public CommentsResponse selectComments(long postId) {
        Post post = postRepository.findByPostId(postId).orElseThrow(() -> new NoSuchPostException(ErrorCode._400_NO_SUCH_POST));
        List<Comment> parentComments = post.getCommentList().stream()
                .filter(comment -> (comment.getParentId() == null))
                .toList();

        List<CommentResponse> commentResponses = new ArrayList<>();
        for (Comment comment : parentComments) {
            commentResponses.add(
                    CommentResponse.toCommentResponseDto(
                            comment,
                            null
                    )
            );
        }

        Collections.sort(commentResponses);

        return new CommentsResponse(commentResponses);
    }

    @Transactional
    public void saveReComment(
            CustomUserDetails customUserDetails,
            long postId,
            long commentId,
            WriteReCommentRequest writeReCommentRequest) {
        Member mergedMember = entityManager.merge(customUserDetails.getMember());
        Post post = postRepository.findByPostId(postId).orElseThrow(() -> new NoSuchPostException(ErrorCode._400_NO_SUCH_POST));
        commentRepository.findByCommentId(commentId).orElseThrow(() -> new NoSuchCommentException(ErrorCode._400_NO_SUCH_COMMENT));

        Comment reComment = Comment.createReComment(post, mergedMember,
                commentId, writeReCommentRequest);

        entityManager.persist(reComment);
    }

    public CommentsResponse selectReComments(long commentId) {
        commentRepository.findByCommentId(commentId).orElseThrow(() -> new NoSuchCommentException(ErrorCode._400_NO_SUCH_COMMENT));

        List<Comment> comments = commentRepository.findByParentId(commentId);
        List<CommentResponse> commentResponses = new ArrayList<>();

        for (Comment comment : comments) {
            String mentionedNickname = null;

            if (comment.getMentionedMember() != null) {
                Member member = memberRepository.findByMemberId(comment.getMentionedMember()).orElseGet(() -> null);
                mentionedNickname = (member == null) ? "(unknown)":member.getNickname();
            }

            commentResponses.add(
                    CommentResponse.toCommentResponseDto(comment, mentionedNickname)
            );
        }

        Collections.sort(commentResponses);

        return new CommentsResponse(commentResponses);
    }
}
