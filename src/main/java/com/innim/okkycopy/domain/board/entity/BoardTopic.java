package com.innim.okkycopy.domain.board.entity;

import com.innim.okkycopy.domain.board.community.entity.CommunityPost;
import com.innim.okkycopy.domain.board.event.entity.EventPost;
import com.innim.okkycopy.domain.board.knowledge.entity.KnowledgePost;
import com.innim.okkycopy.domain.board.qna.entity.QnaPost;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

    @OneToMany(mappedBy = "boardTopic")
    List<KnowledgePost> knowledgePosts;

    @OneToMany(mappedBy = "boardTopic")
    List<CommunityPost> communityPosts;

    @OneToMany(mappedBy = "boardTopic")
    List<EventPost> eventPosts;

    @OneToMany(mappedBy = "boardTopic")
    List<QnaPost> qnaPosts;

    @OneToMany(mappedBy = "boardTopic")
    List<Tag> tags;

}
