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
public class CommentDetailsResponse implements Comparable<CommentDetailsResponse> {

    private Long commentId;
    private WriterInfo writerInfo;
    private String content;
    private LocalDateTime createdDate;
    private LocalDateTime lastUpdate;
    private Integer likes;
    private Integer hates;
    private RequesterInfo requesterInfo;
    private ReCommentListResponse reComments;

    public static CommentDetailsResponse of(
        Comment comment,
        ReCommentListResponse reComments,
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
            .reComments(reComments)
            .build();
    }

    @Override
    public int compareTo(CommentDetailsResponse c) {
        if (this.createdDate.isBefore(c.createdDate)) {
            return -1;
        } else if (this.createdDate.isAfter(c.createdDate)) {
            return 1;
        }
        return 0;
    }

}
