package com.innim.okkycopy.domain.board.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CommentRequest {
    @Size(min = 1, max = 20000)
    private String content;
}
