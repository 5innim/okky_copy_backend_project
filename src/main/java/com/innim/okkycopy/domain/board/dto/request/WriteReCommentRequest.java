package com.innim.okkycopy.domain.board.dto.request;

import com.innim.okkycopy.global.commons.validation.annotation.MentionId;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class WriteReCommentRequest {
    @Size(min = 1, max = 20000)
    private String content;
    @MentionId
    private Long mentionId;
}
