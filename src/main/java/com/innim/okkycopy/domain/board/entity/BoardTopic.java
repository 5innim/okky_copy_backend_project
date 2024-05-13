package com.innim.okkycopy.domain.board.entity;

import com.innim.okkycopy.domain.board.community.entity.CommunityPost;
import com.innim.okkycopy.domain.board.community.entity.CommunityTag;
import com.innim.okkycopy.domain.board.event.entity.EventPost;
import com.innim.okkycopy.domain.board.event.entity.EventTag;
import com.innim.okkycopy.domain.board.knowledge.entity.KnowledgePost;
import com.innim.okkycopy.domain.board.knowledge.entity.KnowledgeTag;
import com.innim.okkycopy.domain.board.qna.entity.QnaPost;
import com.innim.okkycopy.domain.board.qna.entity.QnaTag;
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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Setter
@Getter
@Entity
@Table(name = "board_topic")
public class BoardTopic {

    @Id
    @Column(name = "topic_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long topicId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "type_id", insertable = false, updatable = false)
    private BoardType boardType;

    @Column(nullable = false, unique = true)
    private String name;

    @OneToMany(mappedBy = "boardTopic", fetch = FetchType.LAZY)
    List<KnowledgePost> knowledgePosts;

    @OneToMany(mappedBy = "boardTopic", fetch = FetchType.LAZY)
    List<CommunityPost> communityPosts;

    @OneToMany(mappedBy = "boardTopic", fetch = FetchType.LAZY)
    List<EventPost> eventPosts;

    @OneToMany(mappedBy = "boardTopic", fetch = FetchType.LAZY)
    List<QnaPost> qnaPosts;

    @OneToMany(mappedBy = "boardTopic", fetch = FetchType.LAZY)
    List<KnowledgeTag> knowledgeTags;

    @OneToMany(mappedBy = "boardTopic", fetch = FetchType.LAZY)
    List<CommunityTag> communityTags;

    @OneToMany(mappedBy = "boardTopic", fetch = FetchType.LAZY)
    List<EventTag> eventTags;

    @OneToMany(mappedBy = "boardTopic", fetch = FetchType.LAZY)
    List<QnaTag> qnaTags;
}
