package com.innim.okkycopy.domain.board.comment.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class RequesterInfo {

    private boolean like;
    private boolean hate;
}
