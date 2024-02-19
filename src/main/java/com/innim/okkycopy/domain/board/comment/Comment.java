package com.innim.okkycopy.domain.board.comment;

import com.innim.okkycopy.domain.board.dto.request.WriteCommentRequest;
import com.innim.okkycopy.domain.board.dto.request.WriteReCommentRequest;
import com.innim.okkycopy.domain.board.entity.Post;
import com.innim.okkycopy.domain.board.knowledge.entity.KnowledgePost;
import com.innim.okkycopy.domain.member.entity.Member;
import jakarta.persistence.*;

import java.time.LocalDateTime;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;

@Entity
@Table(name = "comment")
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Inheritance(strategy = InheritanceType.JOINED)
@DynamicInsert
public class Comment {
    @Id
    @Column(name = "comment_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;
    @Column(name = "created_date", nullable = false)
    @CreationTimestamp
    private LocalDateTime createdDate;
    @Column(name = "last_update")
    private LocalDateTime lastUpdate;
    @Column(name = "likes", nullable = false)
    private Long likes;
    @Column(name = "content", nullable = false, length = 20000)
    private String content;
    @Column(name = "parent_id")
    private Long parentId;
    @Column(name = "mentioned_member")
    private Long mentionedMember;

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;
    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    public static Comment createComment(Post post, Member member, WriteCommentRequest writeCommentRequest) {
        Comment comment = Comment.builder()
                .content(writeCommentRequest.getContent())
                .likes(0L)
                .post(post)
                .member(member).build();

        if (post instanceof KnowledgePost)
            ((KnowledgePost) post).setComments(((KnowledgePost) post).getComments() + 1);

        return comment;
    }

    public static void removeComment(Comment comment, EntityManager entityManager) {
        Post post = comment.getPost();
        if (post instanceof KnowledgePost)
            ((KnowledgePost) post).setComments(((KnowledgePost) post).getComments() - 1);

        entityManager.remove(comment);
    }

    public static Comment createReComment(Post post, Member member, long parentId, WriteReCommentRequest writeReCommentRequest) {
        Comment comment = Comment.builder()
            .content(writeReCommentRequest.getContent())
            .mentionedMember(writeReCommentRequest.getMentionId())
            .parentId(parentId)
            .post(post)
            .member(member)
            .likes(0L)
            .build();

        return comment;
    }

}
