package com.innim.okkycopy.domain.board.comment.entity;

import com.innim.okkycopy.domain.board.entity.Post;
import com.innim.okkycopy.domain.board.enums.ExpressionType;
import com.innim.okkycopy.domain.board.qna.entity.QnaPost;
import com.innim.okkycopy.domain.member.entity.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;
import org.hibernate.proxy.HibernateProxy;

@Entity
@Getter
@Setter
@Builder
@IdClass(CommentExpressionKey.class)
@Table(name = "comment_expression")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentExpression {

    @Id
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    private Comment comment;

    @Id
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "expression_type")
    @Enumerated(value = EnumType.ORDINAL)
    private ExpressionType expressionType;




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

    // TODO: this method should be expended when new post domain is added
    public static boolean isNotSupportedCase(Comment comment) {
        Post post = comment.getPost();

        // do unproxy post, because post field in Comment is FetchType.LAZY.
        if (post instanceof HibernateProxy) {
            post = (Post) Hibernate.unproxy(post);
        }

        return post instanceof QnaPost && comment.getDepth() > 1;
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


