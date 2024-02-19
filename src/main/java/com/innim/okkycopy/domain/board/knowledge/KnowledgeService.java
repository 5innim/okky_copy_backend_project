package com.innim.okkycopy.domain.board.knowledge;

import com.innim.okkycopy.domain.board.dto.request.WriteCommentRequest;
import com.innim.okkycopy.domain.board.dto.request.WriteReCommentRequest;
import com.innim.okkycopy.domain.board.dto.request.write.WriteRequest;
import com.innim.okkycopy.domain.board.dto.response.comments.CommentResponse;
import com.innim.okkycopy.domain.board.dto.response.comments.CommentsResponse;
import com.innim.okkycopy.domain.board.dto.response.post.detail.PostDetailResponse;
import com.innim.okkycopy.domain.board.entity.BoardTopic;
import com.innim.okkycopy.domain.board.knowledge.entity.KnowledgeComment;
import com.innim.okkycopy.domain.board.knowledge.entity.KnowledgePost;
import com.innim.okkycopy.domain.board.knowledge.repository.KnowledgeCommentRepository;
import com.innim.okkycopy.domain.board.knowledge.repository.KnowledgePostRepository;
import com.innim.okkycopy.domain.board.repository.BoardTopicRepository;
import com.innim.okkycopy.domain.member.MemberRepository;
import com.innim.okkycopy.domain.member.entity.Member;
import com.innim.okkycopy.global.auth.CustomUserDetails;
import com.innim.okkycopy.global.error.ErrorCode;
import com.innim.okkycopy.global.error.exception.NoAuthorityException;
import com.innim.okkycopy.global.error.exception.NoSuchCommentException;
import com.innim.okkycopy.global.error.exception.NoSuchPostException;
import com.innim.okkycopy.global.error.exception.NoSuchTopicException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class KnowledgeService {

    private final BoardTopicRepository boardTopicRepository;
    private final KnowledgePostRepository knowledgePostRepository;
    private final KnowledgeCommentRepository knowledgeCommentRepository;
    private final MemberRepository memberRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void saveKnowledgePost(WriteRequest writeRequest, CustomUserDetails customUserDetails) {
        Member member = entityManager.merge(customUserDetails.getMember());

        BoardTopic boardTopic = boardTopicRepository.findByName(writeRequest.getTopic()).orElseThrow(() -> new NoSuchTopicException(
            ErrorCode._400_NO_SUCH_TOPIC));

        KnowledgePost knowledgePost = KnowledgePost.createKnowledgePost(writeRequest, boardTopic, member);
        entityManager.persist(knowledgePost);
    }

    public PostDetailResponse selectKnowledgePost(long postId) {
        KnowledgePost knowledgePost = knowledgePostRepository.findByPostId(postId).orElseThrow(() -> new NoSuchPostException(ErrorCode._400_NO_SUCH_POST));
        Member member = memberRepository.findByMemberId(knowledgePost.getMember().getMemberId()).orElseGet(() -> Member.builder().memberId(0l)
            .build());

        return PostDetailResponse.toPostDetailResponseDto(knowledgePost, member);
    }

    @Transactional
    public void updateKnowledgePost(CustomUserDetails customUserDetails, WriteRequest updateRequest, long postId) {
        Member mergedMember = entityManager.merge(customUserDetails.getMember());
        KnowledgePost knowledgePost = knowledgePostRepository.findByPostId(postId).orElseThrow(() -> new NoSuchPostException(ErrorCode._400_NO_SUCH_POST));
        BoardTopic boardTopic = boardTopicRepository.findByName(updateRequest.getTopic()).orElseThrow(() -> new NoSuchTopicException(
            ErrorCode._400_NO_SUCH_TOPIC));

        if (knowledgePost.getMember().getMemberId() != mergedMember.getMemberId()) throw new NoAuthorityException(ErrorCode._403_NO_AUTHORITY);
        knowledgePost.updateKnowledgePost(updateRequest, boardTopic);
    }

    @Transactional
    public void deleteKnowledgePost(CustomUserDetails customUserDetails, long postId) {
        Member mergedMember = entityManager.merge(customUserDetails.getMember());
        KnowledgePost knowledgePost = knowledgePostRepository.findByPostId(postId).orElseThrow(() -> new NoSuchPostException(ErrorCode._400_NO_SUCH_POST));

        if (knowledgePost.getMember().getMemberId() != mergedMember.getMemberId()) throw new NoAuthorityException(ErrorCode._403_NO_AUTHORITY);
        entityManager.remove(knowledgePost);
    }

    @Transactional
    public void saveKnowledgeComment(CustomUserDetails customUserDetails,
        WriteCommentRequest writeCommentRequest,
        long postId) {
        Member mergedMember = entityManager.merge(customUserDetails.getMember());
        KnowledgePost knowledgePost = knowledgePostRepository.findByPostId(postId).orElseThrow(() -> new NoSuchPostException(ErrorCode._400_NO_SUCH_POST));

        KnowledgeComment comment = KnowledgeComment.createKnowledgeComment(knowledgePost, mergedMember,
            writeCommentRequest);
        entityManager.persist(comment);

        knowledgePost.setComments(knowledgePost.getComments() + 1);
    }

    @Transactional
    public void updateKnowledgeComment(CustomUserDetails customUserDetails,
        WriteCommentRequest writeCommentRequest,
        long commentId) {
        Member mergedMember = entityManager.merge(customUserDetails.getMember());
        KnowledgeComment comment = knowledgeCommentRepository.findByCommentId(commentId).orElseThrow(() -> new NoSuchCommentException(ErrorCode._400_NO_SUCH_COMMENT));

        if (comment.getMember().getMemberId() != mergedMember.getMemberId()) throw new NoAuthorityException(ErrorCode._403_NO_AUTHORITY);
        comment.setContent(writeCommentRequest.getContent());
        comment.setLastUpdate(LocalDateTime.now());
    }

    @Transactional
    public void deleteKnowledgeComment(CustomUserDetails customUserDetails, long commentId) {
        Member mergedMember = entityManager.merge(customUserDetails.getMember());
        KnowledgeComment comment = knowledgeCommentRepository.findByCommentId(commentId).orElseThrow(() -> new NoSuchCommentException(ErrorCode._400_NO_SUCH_COMMENT));

        if (comment.getMember().getMemberId() != mergedMember.getMemberId()) throw new NoAuthorityException(ErrorCode._403_NO_AUTHORITY);
        entityManager.remove(comment);
    }

    public CommentsResponse selectKnowledgeComments(long postId) {
        KnowledgePost knowledgePost = knowledgePostRepository.findByPostId(postId).orElseThrow(() -> new NoSuchPostException(ErrorCode._400_NO_SUCH_POST));
        List<KnowledgeComment> parentComments = knowledgePost.getCommentList().stream()
            .filter(comment -> ((KnowledgeComment) comment).getParentId() == null)
            .map(comment -> (KnowledgeComment) comment)
            .collect(Collectors.toList());

        List<CommentResponse> commentResponses = new ArrayList<>();
        for (KnowledgeComment comment : parentComments) {
            commentResponses.add(
                CommentResponse.toCommentResponseDto(
                    comment,
                    comment.getContent(),
                    comment.getLikes())
            );
        }

        Collections.sort(commentResponses);

        return new CommentsResponse(commentResponses);
    }

    @Transactional
    public void saveKnowledgeReComment(
        CustomUserDetails customUserDetails,
        long postId,
        long commentId,
        WriteReCommentRequest writeReCommentRequest) {
        Member mergedMember = entityManager.merge(customUserDetails.getMember());
        KnowledgePost knowledgePost = knowledgePostRepository.findByPostId(postId).orElseThrow(() -> new NoSuchPostException(ErrorCode._400_NO_SUCH_POST));
        knowledgeCommentRepository.findByCommentId(commentId).orElseThrow(() -> new NoSuchCommentException(ErrorCode._400_NO_SUCH_COMMENT));

        KnowledgeComment reComment = KnowledgeComment.createKnowledgeReComment(knowledgePost, mergedMember,
            commentId, writeReCommentRequest);

        entityManager.persist(reComment);
    }



}
