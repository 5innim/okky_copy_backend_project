package com.innim.okkycopy.domain.board.comment.dto.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReCommentListResponse {

    private List<ReCommentDetailsResponse> comments;

}
