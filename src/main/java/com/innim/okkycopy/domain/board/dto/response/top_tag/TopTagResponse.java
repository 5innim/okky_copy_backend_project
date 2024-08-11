package com.innim.okkycopy.domain.board.dto.response.top_tag;

import com.innim.okkycopy.domain.board.entity.TopTag;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class TopTagResponse {

    private String name;
    private long cnt;

    public static TopTagResponse from(TopTag topTag) {
        return TopTagResponse.builder()
            .name(topTag.getName())
            .cnt(topTag.getCreates())
            .build();
    }
}
