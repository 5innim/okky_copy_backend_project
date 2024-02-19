package com.innim.okkycopy.domain.board.dto.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class WriteCommentRequest {
    @Size(min = 1, max = 20000)
    private String content;
}
