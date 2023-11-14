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
public class Type {
    private List<Topic> topics;

    public static Type toDto(BoardType boardType) {
        List<Topic> topicList = new ArrayList<>();
        for (BoardTopic boardTopic : boardType.getBoardTopics()) {
            topicList.add(new Topic(boardTopic.getName()));
        }
        return new Type(topicList);
    }

}
