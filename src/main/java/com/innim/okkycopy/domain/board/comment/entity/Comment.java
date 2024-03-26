package com.innim.okkycopy.domain.board.comment.entity;

import com.innim.okkycopy.domain.board.comment.dto.request.CommentAddRequest;
import com.innim.okkycopy.domain.board.comment.dto.request.ReCommentAddRequest;
import com.innim.okkycopy.domain.board.entity.Post;
import com.innim.okkycopy.domain.board.knowledge.entity.KnowledgePost;
import com.innim.okkycopy.domain.member.entity.Member;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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
    private Integer likes;
    @Column(name = "hates", nullable = false)
    private Integer hates;
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
    @OneToMany(mappedBy = "comment", cascade = {CascadeType.REMOVE})
    private List<CommentExpression> commentExpressionList;

    public void update(String content) {
        this.content = content;
        this.lastUpdate = LocalDateTime.now();
    }

    public static Comment create(Post post, Member member, CommentAddRequest commentAddRequest) {
        Comment comment = Comment.builder()
            .content(commentAddRequest.getContent())
            .likes(0)
            .hates(0)
            .post(post)
            .member(member).build();

        if (post instanceof KnowledgePost) {
            ((KnowledgePost) post).setComments(((KnowledgePost) post).getComments() + 1);
        }

        return comment;
    }

    public static void remove(Comment comment, EntityManager entityManager) {
        Post post = comment.getPost();
        if (post instanceof KnowledgePost) {
            ((KnowledgePost) post).setComments(((KnowledgePost) post).getComments() - 1);
        }

        entityManager.remove(comment);
    }

    public static Comment createReComment(Post post, Member member, long parentId,
        ReCommentAddRequest reCommentAddRequest) {
        return Comment.builder()
            .content(reCommentAddRequest.getContent())
            .mentionedMember(reCommentAddRequest.getMentionId())
            .parentId(parentId)
            .post(post)
            .member(member)
            .likes(0)
            .hates(0)
            .build();
    }

    public void increaseLikes() {
        this.likes += 1;
    }

    public void decreaseLikes() {
        this.likes -= 1;
    }

    public void increaseHates() {
        this.hates += 1;
    }

    public void decreaseHates() {
        this.hates -= 1;
    }


}
