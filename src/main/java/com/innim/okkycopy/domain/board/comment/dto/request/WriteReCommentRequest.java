package com.innim.okkycopy.domain.board.comment.dto.request;

import com.innim.okkycopy.global.commons.validation.annotation.MentionId;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WriteReCommentRequest {
    @Size(min = 1, max = 20000)
    private String content;
    @MentionId
    private Long mentionId;
}
