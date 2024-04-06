package com.innim.okkycopy.domain.board.community.entity;

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
@Table(name = "community_tag")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Setter
@DiscriminatorValue(value = "community")
public class CommunityTag extends Tag {

    @ManyToOne(optional = false)
    @JoinColumn(name = "topic_id")
    private BoardTopic boardTopic;

    public static CommunityTag of(CommunityPost communityPost, BoardTopic boardTopic, String name) {
        CommunityTag communityTag = new CommunityTag();
        communityTag.setPost((Post) communityPost);
        communityTag.setName(name);
        communityTag.setBoardTopic(boardTopic);

        return communityTag;
    }
}
