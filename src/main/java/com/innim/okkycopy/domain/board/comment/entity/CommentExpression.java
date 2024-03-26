package com.innim.okkycopy.domain.board.comment.entity;

import com.innim.okkycopy.domain.board.entity.Post;
import com.innim.okkycopy.domain.board.enums.ExpressionType;
import com.innim.okkycopy.domain.board.knowledge.entity.KnowledgePost;
import com.innim.okkycopy.domain.member.entity.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

@Entity
@Getter
@Builder
@DynamicInsert
@Table(name = "comment_expression", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"member_id", "comment_id"})})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CommentExpression {

    @Id
    @Column(name = "expression_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long expressionId;
    @Column(name = "expression_type")
    @Enumerated(value = EnumType.ORDINAL)
    private ExpressionType expressionType;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    private Comment comment;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;


    public static CommentExpression of(Comment comment, Member member, ExpressionType type) {
        if (type.equals(ExpressionType.LIKE)) {
            comment.increaseLikes();
        } else if (type.equals(ExpressionType.HATE)) {
            comment.increaseHates();
        }
        return CommentExpression.builder()
            .comment(comment)
            .member(member)
            .expressionType(type)
            .build();
    }

    public static boolean isNotSupportedCase(Comment comment) {
        Post post = comment.getPost();
        if (post instanceof KnowledgePost) {
            return false;
        }
        // else if (post instance of QnAPost && comment.getParentId() != null)

        return true;
    }

    public static void remove(
        EntityManager entityManager,
        CommentExpression commentExpression,
        Comment comment,
        ExpressionType type) {
        if (type.equals(ExpressionType.LIKE)) {
            comment.decreaseLikes();
        } else if (type.equals(ExpressionType.HATE)) {
            comment.decreaseHates();
        }
        entityManager.remove(commentExpression);
    }
}


