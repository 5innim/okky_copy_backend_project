package com.innim.okkycopy.domain.board.dto.response.post.detail;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class PostRequesterInfoResponse {

    private boolean scrap;
    private boolean like;
    private boolean hate;
}
