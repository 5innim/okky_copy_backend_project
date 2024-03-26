package com.innim.okkycopy.domain.board.dto.response.topics;

import com.innim.okkycopy.domain.board.entity.BoardType;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TopicListResponse {

    List<TypeDetailsResponse> types;

    public static TopicListResponse of(List<BoardType> boardTypes) {
        List<TypeDetailsResponse> typeList = new ArrayList<>();
        for (BoardType boardType : boardTypes) {
            typeList.add(TypeDetailsResponse.of(boardType));
        }

        return new TopicListResponse(typeList);
    }
}
