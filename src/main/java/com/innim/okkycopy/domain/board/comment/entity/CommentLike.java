package com.innim.okkycopy.domain.board.comment.entity;

import com.innim.okkycopy.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

@Entity
@Table(name = "comment_like",  uniqueConstraints = {
        @UniqueConstraint(columnNames = {"member_id", "comment_id"})})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicInsert
public class CommentLike {
    @Id
    @Column(name = "like_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long likeId;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    private Comment comment;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;
}
