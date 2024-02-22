package com.innim.okkycopy.domain.board.entity;

import com.innim.okkycopy.domain.board.enums.ExpressionType;
import com.innim.okkycopy.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

@Entity
@Table(name = "post_expression", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"member_id", "post_id"})})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicInsert
@Getter
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


}
