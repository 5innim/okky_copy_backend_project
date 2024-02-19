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
public class TopicsResponse {
    List<TypeResponse> types;

    public static TopicsResponse toDto(List<BoardType> boardTypes) {
        List<TypeResponse> typeList = new ArrayList<>();
        for (BoardType boardType : boardTypes) {
            typeList.add(TypeResponse.toDto(boardType));
        }

        return new TopicsResponse(typeList);
    }
}
