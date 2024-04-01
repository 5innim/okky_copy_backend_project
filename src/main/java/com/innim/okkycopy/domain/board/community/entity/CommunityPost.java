package com.innim.okkycopy.domain.board.community.entity;

import com.innim.okkycopy.domain.board.entity.BoardTopic;
import com.innim.okkycopy.domain.board.entity.Post;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@DynamicUpdate
@DiscriminatorValue(value = "community")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "community_post")
@AllArgsConstructor
@Builder
@Setter
@Getter
@SuperBuilder
public class CommunityPost extends Post {

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

}
