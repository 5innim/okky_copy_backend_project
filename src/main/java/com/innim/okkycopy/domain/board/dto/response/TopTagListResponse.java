package com.innim.okkycopy.domain.board.dto.response;


import com.innim.okkycopy.domain.board.entity.TopTag;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class TopTagListResponse {

    private List<TopTagResponse> tags;

    public static TopTagListResponse from(List<TopTag> topTagList) {
        ArrayList<TopTagResponse> tags = new ArrayList<>();
        for (TopTag tag : topTagList) {
            tags.add(TopTagResponse.from(tag));
        }

        return TopTagListResponse.builder()
            .tags(tags)
            .build();
    }



    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    static class TopTagResponse {

        private String name;
        private int cnt;

        public static TopTagResponse from(TopTag topTag) {
            return TopTagResponse.builder()
                .name(topTag.getName())
                .cnt(topTag.getCreates())
                .build();
        }
    }
}


