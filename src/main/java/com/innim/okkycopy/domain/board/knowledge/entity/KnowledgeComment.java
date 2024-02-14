package com.innim.okkycopy.domain.board.knowledge.entity;

import com.innim.okkycopy.domain.board.dto.request.WriteCommentRequest;
import com.innim.okkycopy.domain.board.entity.Comment;
import com.innim.okkycopy.domain.board.entity.Post;
import com.innim.okkycopy.domain.member.entity.Member;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Table(name = "knowledge_comment")
@Setter
@Builder
@DiscriminatorValue(value = "knowledge")
@DynamicUpdate
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class KnowledgeComment extends Comment {
    @Column(name = "likes", nullable = false)
    private Long likes;
    @Column(name = "content", nullable = false, length = 20000)
    private String content;
    @Column(name = "parent_id")
    private Long parentId;

    public static KnowledgeComment createKnowledgeComment(Post post, Member member, WriteCommentRequest writeCommentRequest) {
        KnowledgeComment comment = KnowledgeComment.builder()
            .content(writeCommentRequest.getContent())
            .build();
        comment.setPost(post);
        comment.setMember(member);
        comment.setLikes(0l);
        return comment;
    }
}

