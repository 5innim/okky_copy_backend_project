package com.innim.okkycopy.domain.board.entity;

import com.innim.okkycopy.domain.board.knowledge.entity.KnowledgePost;
import com.innim.okkycopy.domain.board.knowledge.entity.KnowledgeTag;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "board_topic")
public class BoardTopic {
    @Id
    @Column(name = "topic_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long topicId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "type_id", insertable = false, updatable = false)
    private BoardType boardType;

    @Column(nullable = false, unique = true)
    private String name;

    @OneToMany(mappedBy = "boardTopic", fetch = FetchType.LAZY)
    List<KnowledgePost> knowledgePosts;

    @OneToMany(mappedBy = "boardTopic", fetch = FetchType.LAZY)
    List<KnowledgeTag> knowledgeTags;

}
