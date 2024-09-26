package com.innim.okkycopy.domain.board.comment.dto.request;

import com.innim.okkycopy.global.common.validation.annotation.MentionId;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class ReCommentRequest {

    @Size(min = 1, max = 20000)
    private String content;
    @MentionId
    private Long mentionId;
}
