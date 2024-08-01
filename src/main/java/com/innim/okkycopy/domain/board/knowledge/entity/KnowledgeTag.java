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
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "knowledge_tag")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Setter
@DiscriminatorValue(value = "knowledge")
@SuperBuilder
public class KnowledgeTag extends Tag {

    @ManyToOne(optional = false)
    @JoinColumn(name = "topic_id")
    private BoardTopic boardTopic;

    public static KnowledgeTag of(KnowledgePost knowledgePost, BoardTopic boardTopic, String name) {
        return KnowledgeTag.builder()
            .post((Post) knowledgePost)
            .name(name)
            .boardTopic(boardTopic)
            .build();
    }
}
