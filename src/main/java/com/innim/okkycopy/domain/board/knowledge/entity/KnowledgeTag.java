package com.innim.okkycopy.domain.board.knowledge.entity;

import com.innim.okkycopy.domain.board.entity.BoardTopic;
import com.innim.okkycopy.domain.board.entity.Post;
import com.innim.okkycopy.domain.board.entity.Tag;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "knowledge_tag")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Setter
@DiscriminatorValue(value = "knowledge")
public class KnowledgeTag extends Tag {
    @ManyToOne(optional = false)
    @JoinColumn(name = "topic_id")
    private BoardTopic boardTopic;

    public static KnowledgeTag createKnowledgeTag(KnowledgePost knowledgePost, BoardTopic boardTopic, String name) {
        KnowledgeTag knowledgeTag = new KnowledgeTag();
        knowledgeTag.setPost((Post) knowledgePost);
        knowledgeTag.setName(name);
        knowledgeTag.setBoardTopic(boardTopic);

        return knowledgeTag;
    }
}
