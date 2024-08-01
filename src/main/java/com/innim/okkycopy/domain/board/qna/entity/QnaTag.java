package com.innim.okkycopy.domain.board.qna.entity;

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
@Table(name = "qna_tag")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Setter
@DiscriminatorValue(value = "qna")
@SuperBuilder
public class QnaTag extends Tag {

    @ManyToOne(optional = false)
    @JoinColumn(name = "topic_id")
    private BoardTopic boardTopic;

    public static QnaTag of(QnaPost qnaPost, BoardTopic boardTopic, String name) {
        return QnaTag.builder()
            .post((Post) qnaPost)
            .name(name)
            .boardTopic(boardTopic)
            .build();
    }

}
