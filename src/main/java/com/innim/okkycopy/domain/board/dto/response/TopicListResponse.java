package com.innim.okkycopy.domain.board.dto.response;

import com.innim.okkycopy.domain.board.entity.BoardTopic;
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

    private List<TypeDetailsResponse> types;

    public static TopicListResponse from(List<BoardType> boardTypes) {
        List<TypeDetailsResponse> typeList = new ArrayList<>();
        for (BoardType boardType : boardTypes) {
            typeList.add(TypeDetailsResponse.from(boardType));
        }

        return new TopicListResponse(typeList);
    }

    @Getter
    @AllArgsConstructor
    static class TypeDetailsResponse {

        private String name;
        private long typeId;
        private List<TopicDetailsResponse> topics;

        public static TypeDetailsResponse from(BoardType boardType) {
            List<TopicDetailsResponse> topicList = new ArrayList<>();

            for (BoardTopic boardTopic : boardType.getBoardTopics()) {
                topicList.add(new TopicDetailsResponse(boardTopic.getName(), boardTopic.getTopicId()));
            }
            return new TypeDetailsResponse(boardType.getName(), boardType.getTypeId(), topicList);
        }
    }

    @Getter
    @AllArgsConstructor
    static class TopicDetailsResponse {

        private String name;
        private long topicId;
    }
}
