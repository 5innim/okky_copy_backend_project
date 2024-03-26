package com.innim.okkycopy.domain.board.entity;

import com.innim.okkycopy.domain.board.enums.ExpressionType;
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
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;

@Entity
@Getter
@Setter
@Builder
@Table(name = "post_expression", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"member_id", "post_id"})})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@DynamicInsert
public class PostExpression {

    @Id
    @Column(name = "expression_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long expressionId;
    @Column(name = "expression_type")
    @Enumerated(value = EnumType.ORDINAL)
    private ExpressionType expressionType;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    public static PostExpression of(Post post, Member member, ExpressionType type) {
        if (type.equals(ExpressionType.LIKE)) {
            post.increaseLikes();
        } else if (type.equals(ExpressionType.HATE)) {
            post.increaseHates();
        }
        return PostExpression.builder()
            .post(post)
            .member(member)
            .expressionType(type)
            .build();
    }

    public static void remove(
        EntityManager entityManager,
        PostExpression postExpression,
        Post post,
        ExpressionType type) {
        if (type.equals(ExpressionType.LIKE)) {
            post.decreaseLikes();
        } else if (type.equals(ExpressionType.HATE)) {
            post.decreaseHates();
        }
        entityManager.remove(postExpression);
    }


}
