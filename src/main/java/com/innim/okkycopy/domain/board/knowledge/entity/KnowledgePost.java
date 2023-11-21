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
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

@DynamicUpdate
@Entity
@Setter
@Table(name = "knowledge_post")
@DiscriminatorValue(value = "knowledge")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class KnowledgePost extends Post {
    @ManyToOne(optional = false)
    @JoinColumn(name = "topic_id")
    private BoardTopic boardTopic;
    @Column(nullable = false)
    private int likes;
    @Column(nullable = false)
    private int scraps;
    @Column(nullable = false)
    private int views;
    @Column(nullable = false)
    private int comments;

    public static KnowledgePost createKnowledgePost(WriteRequest writeRequest, BoardTopic boardTopic, Member member) {
        KnowledgePost knowledgePost = new KnowledgePost();
        List<Tag> tags = new ArrayList<>();
        for (TagRequest tag : writeRequest.getTags()) {
            tags.add(KnowledgeTag.createKnowledgeTag(knowledgePost, boardTopic, tag.getName()));
        }

        member.getPosts().add((Post) knowledgePost);

        knowledgePost.setMember(member);
        knowledgePost.setContent(writeRequest.getContent());
        knowledgePost.setTags(tags);
        knowledgePost.setTitle(writeRequest.getTitle());
        knowledgePost.setLastUpdate(null);
        knowledgePost.setBoardTopic(boardTopic);
        knowledgePost.setLikes(0);
        knowledgePost.setScraps(0);
        knowledgePost.setViews(0);
        knowledgePost.setComments(0);

        return knowledgePost;
    }

}
