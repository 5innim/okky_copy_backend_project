package com.innim.okkycopy.domain.board.qna.entity;

import com.innim.okkycopy.domain.board.dto.request.write.PostRequest;
import com.innim.okkycopy.domain.board.dto.request.write.TagInfo;
import com.innim.okkycopy.domain.board.entity.BoardTopic;
import com.innim.okkycopy.domain.board.entity.Post;
import com.innim.okkycopy.domain.board.entity.Tag;
import com.innim.okkycopy.domain.member.entity.Member;
import com.innim.okkycopy.global.error.ErrorCase;
import com.innim.okkycopy.global.error.exception.StatusCode400Exception;
import com.innim.okkycopy.global.error.exception.StatusCodeException;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@DiscriminatorValue(value = "qna")
@Setter
@Getter
@SuperBuilder
@AllArgsConstructor
public class QnaPost extends Post {

    public static QnaPost of(PostRequest postRequest, BoardTopic boardTopic, Member member)
        throws StatusCodeException {
        if (isNotSupportedTopic(boardTopic)) {
            throw new StatusCode400Exception(ErrorCase._400_BAD_FORM_DATA);
        }
        QnaPost qnaPost = QnaPost.builder()
            .member(member)
            .content(postRequest.getContent())
            .title(postRequest.getTitle())
            .lastUpdate(null)
            .boardTopic(boardTopic)
            .likes(0)
            .hates(0)
            .scraps(0)
            .views(0)
            .comments(0)
            .build();

        List<Tag> tags = new ArrayList<>();
        for (TagInfo tag : postRequest.getTags()) {
            tags.add(Tag.of(qnaPost, tag.getName()));
        }
        member.getPosts().add((Post) qnaPost);
        qnaPost.setTags(tags);

        return qnaPost;
    }

    public void update(PostRequest updateRequest, BoardTopic boardTopic) {
        if (isNotSupportedTopic(boardTopic)) {
            throw new StatusCode400Exception(ErrorCase._400_BAD_FORM_DATA);
        }
        this.setTitle(updateRequest.getTitle());
        this.setContent(updateRequest.getContent());
        this.setBoardTopic(boardTopic);
        this.setLastUpdate(LocalDateTime.now());

        List<Tag> tags = this.getTags();
        tags.clear();

        for (TagInfo tag : updateRequest.getTags()) {
            tags.add(Tag.of(this, tag.getName()));
        }
    }

    public static boolean isNotSupportedTopic(BoardTopic boardTopic) {
        return !boardTopic.getBoardType().getName().equals("Q&A");
    }

}
