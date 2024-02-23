package com.innim.okkycopy.domain.board.comment.entity;

import com.innim.okkycopy.domain.board.entity.Post;
import com.innim.okkycopy.domain.board.enums.ExpressionType;
import com.innim.okkycopy.domain.board.knowledge.entity.KnowledgePost;
import com.innim.okkycopy.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;
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


    public static CommentExpression createCommentExpression(Comment comment, Member member, ExpressionType type) {
        if (type.equals(ExpressionType.LIKE)) comment.increaseLikes();
        else if (type.equals(ExpressionType.HATE)) comment.increaseHates();
        return CommentExpression.builder()
                .comment(comment)
                .member(member)
                .expressionType(type)
                .build();
    }

    public static boolean isNotSupportedCase(Comment comment) {
        Post post = comment.getPost();
        if (post instanceof KnowledgePost) return false;
        // else if (post instance of QnAPost && comment.getParentId() != null)

        return true;
    }
}


