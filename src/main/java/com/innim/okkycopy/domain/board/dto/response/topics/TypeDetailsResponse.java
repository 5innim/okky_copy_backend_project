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
public class TypeDetailsResponse {

    String name;
    long typeId;
    List<TopicDetailsResponse> topics;

    public static TypeDetailsResponse of(BoardType boardType) {
        List<TopicDetailsResponse> topicList = new ArrayList<>();

        for (BoardTopic boardTopic : boardType.getBoardTopics()) {
            topicList.add(new TopicDetailsResponse(boardTopic.getName(), boardTopic.getTopicId()));
        }
        return new TypeDetailsResponse(boardType.getName(), boardType.getTypeId(), topicList);
    }

}
