package com.innim.okkycopy.domain.board.dto.response.comments;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CommentsResponse {
    private List<CommentResponse> comments;

}
