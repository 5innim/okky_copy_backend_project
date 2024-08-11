package com.innim.okkycopy.domain.board.comment.dto.response;

import com.innim.okkycopy.domain.board.comment.entity.Comment;
import com.innim.okkycopy.domain.board.dto.response.post.WriterInfo;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class CommentDetailsResponse {

    private Long commentId;
    private WriterInfo writerInfo;
    private String content;
    private LocalDateTime createdDate;
    private LocalDateTime lastUpdate;
    private Integer likes;
    private Integer hates;
    private RequesterInfo requesterInfo;

    public static CommentDetailsResponse of(
        Comment comment,
        RequesterInfo requesterInfo) {
        return CommentDetailsResponse.builder()
            .writerInfo((comment.getMember() == null)
                ? null : WriterInfo.from(comment.getMember()))
            .content(comment.getContent())
            .createdDate(comment.getCreatedDate())
            .lastUpdate(comment.getLastUpdate())
            .likes(comment.getLikes())
            .hates(comment.getHates())
            .commentId(comment.getCommentId())
            .requesterInfo(requesterInfo)
            .build();
    }

}
