package com.innim.okkycopy.domain.board.event.entity;

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
@Table(name = "event_tag")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Setter
@DiscriminatorValue(value = "event")
@SuperBuilder
public class EventTag extends Tag {

    @ManyToOne(optional = false)
    @JoinColumn(name = "topic_id")
    private BoardTopic boardTopic;

    public static EventTag of(EventPost eventPost, BoardTopic boardTopic, String name) {
        return EventTag.builder()
            .post((Post) eventPost)
            .name(name)
            .boardTopic(boardTopic)
            .build();
    }

}
