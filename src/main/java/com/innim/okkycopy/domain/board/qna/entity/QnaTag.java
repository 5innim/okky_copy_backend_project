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

@Entity
@Table(name = "qna_tag")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Setter
@DiscriminatorValue(value = "qna")
public class QnaTag extends Tag {

    @ManyToOne(optional = false)
    @JoinColumn(name = "topic_id")
    private BoardTopic boardTopic;

    public static QnaTag of(QnaPost qnaPost, BoardTopic boardTopic, String name) {
        QnaTag qnaTag = new QnaTag();
        qnaTag.setPost((Post) qnaPost);
        qnaTag.setName(name);
        qnaTag.setBoardTopic(boardTopic);

        return qnaTag;
    }

}
