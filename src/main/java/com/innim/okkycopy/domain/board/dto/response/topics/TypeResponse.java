package com.innim.okkycopy.domain.board.dto.response.topics;

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
public class TypeResponse {
    String name;
    long typeId;
    List<TopicResponse> topics;

    public static TypeResponse toDto(BoardType boardType) {
        List<TopicResponse> topicList = new ArrayList<>();

        for (BoardTopic boardTopic : boardType.getBoardTopics()) {
            topicList.add(new TopicResponse(boardTopic.getName(), boardTopic.getTopicId()));
        }
        return new TypeResponse(boardType.getName(), boardType.getTypeId(), topicList);
    }

}
