package com.innim.okkycopy.domain.board.knowledge.entity;

import com.innim.okkycopy.domain.board.dto.request.write.TagRequest;
import com.innim.okkycopy.domain.board.dto.request.write.WriteRequest;
import com.innim.okkycopy.domain.board.entity.BoardTopic;
import com.innim.okkycopy.domain.board.entity.Post;
import com.innim.okkycopy.domain.board.entity.Tag;
import com.innim.okkycopy.domain.member.entity.Member;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicUpdate;

@DynamicUpdate
@Entity
@Setter
@Getter
@SuperBuilder
@Table(name = "knowledge_post")
@DiscriminatorValue(value = "knowledge")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class KnowledgePost extends Post {

    @ManyToOne(optional = false)
    @JoinColumn(name = "topic_id")
    private BoardTopic boardTopic;
    @Column(nullable = false)
    private Integer likes;
    @Column(nullable = false)
    private Integer hates;
    @Column(nullable = false)
    private Integer scraps;
    @Column(nullable = false)
    private Integer views;
    @Column(nullable = false)
    private Integer comments;

    public static KnowledgePost createKnowledgePost(WriteRequest writeRequest, BoardTopic boardTopic, Member member) {
        KnowledgePost knowledgePost = KnowledgePost.builder()
            .member(member)
            .content(writeRequest.getContent())
            .title(writeRequest.getTitle())
            .lastUpdate(null)
            .boardTopic(boardTopic)
            .likes(0)
            .hates(0)
            .scraps(0)
            .views(0)
            .comments(0)
            .build();

        List<Tag> tags = new ArrayList<>();
        for (TagRequest tag : writeRequest.getTags()) {
            tags.add(KnowledgeTag.createKnowledgeTag(knowledgePost, boardTopic, tag.getName()));
        }
        member.getPosts().add((Post) knowledgePost);
        knowledgePost.setTags(tags);

        return knowledgePost;
    }

    public void updateKnowledgePost(WriteRequest updateRequest, BoardTopic boardTopic) {
        this.setTitle(updateRequest.getTitle());
        this.setContent(updateRequest.getContent());
        this.setBoardTopic(boardTopic);
        this.setLastUpdate(LocalDateTime.now());

        List<Tag> tags = this.getTags();
        tags.clear();

        for (TagRequest tag : updateRequest.getTags()) {
            tags.add(KnowledgeTag.createKnowledgeTag(this, boardTopic, tag.getName()));
        }
    }

}
