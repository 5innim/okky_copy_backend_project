package com.innim.okkycopy.domain.board.comment.dto.response;

import com.innim.okkycopy.domain.board.dto.response.post.detail.WriterInfoResponse;
import com.innim.okkycopy.domain.board.comment.entity.Comment;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class CommentResponse implements Comparable<CommentResponse> {
    private Long commentId;
    private String mentionedNickname;
    private WriterInfoResponse writerInfoResponse;
    private String content;
    private LocalDateTime createdDate;
    private LocalDateTime lastUpdate;
    private Long likes;
    private CommentRequesterInfoResponse commentRequesterInfoResponse;

    public static CommentResponse toCommentResponseDto(
            Comment comment,
            String mentionedNickname,
            CommentRequesterInfoResponse commentRequesterInfoResponse) {
        return CommentResponse.builder()
                .writerInfoResponse((comment.getMember()==null) ? null:WriterInfoResponse.toWriterInfoRequestDto(comment.getMember()))
                .mentionedNickname(mentionedNickname)
                .content(comment.getContent())
                .createdDate(comment.getCreatedDate())
                .lastUpdate(comment.getLastUpdate())
                .likes(comment.getLikes())
                .commentId(comment.getCommentId())
                .commentRequesterInfoResponse(commentRequesterInfoResponse)
                .build();
    }

    @Override
    public int compareTo(CommentResponse c) {
        if (this.createdDate.isBefore(c.createdDate)) {
            return -1;
        } else if (this.createdDate.isAfter(c.createdDate)) {
            return 1;
        }
        return 0;
    }

}
