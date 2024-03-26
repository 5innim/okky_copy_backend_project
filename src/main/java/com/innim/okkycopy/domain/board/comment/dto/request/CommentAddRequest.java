package com.innim.okkycopy.domain.board.comment.dto.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CommentAddRequest {

    @Size(min = 1, max = 20000)
    private String content;
}
