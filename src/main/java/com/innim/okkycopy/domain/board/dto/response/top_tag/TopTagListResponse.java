package com.innim.okkycopy.domain.board.dto.response.top_tag;


import com.innim.okkycopy.domain.board.entity.TopTag;
import java.util.ArrayList;
import java.util.List;
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
public class TopTagListResponse {

    List<TopTagResponse> tags;

    public static TopTagListResponse from(List<TopTag> topTagList) {
        ArrayList<TopTagResponse> tags = new ArrayList<>();
        for (TopTag tag : topTagList) {
            tags.add(TopTagResponse.from(tag));
        }

        return TopTagListResponse.builder()
            .tags(tags)
            .build();
    }
}
