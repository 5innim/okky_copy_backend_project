package com.innim.okkycopy.domain.board.comment.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class CommentRequest {

    @Size(min = 1, max = 20000)
    private String content;
}
