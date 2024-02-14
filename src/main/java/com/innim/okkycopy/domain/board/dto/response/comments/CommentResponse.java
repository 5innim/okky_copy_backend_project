package com.innim.okkycopy.domain.board.dto.response.comments;

import com.innim.okkycopy.domain.board.dto.response.post.detail.WriterInfoResponse;
import com.innim.okkycopy.domain.board.entity.Comment;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class CommentResponse {
    private Long commentId;
    private WriterInfoResponse writerInfoResponse;
    private String content;
    private LocalDateTime createdDate;
    private LocalDateTime lastUpdate;
    private Long likes;

    public static CommentResponse toCommentResponseDto(Comment comment, String content, long likes) {
        return CommentResponse.builder()
            .writerInfoResponse((comment.getMember()==null) ? null:WriterInfoResponse.toWriterInfoRequestDto(comment.getMember()))
            .content(content)
            .createdDate(comment.getCreatedDate())
            .lastUpdate(comment.getLastUpdate())
            .likes(likes)
            .commentId(comment.getCommentId())
            .build();
    }
}
