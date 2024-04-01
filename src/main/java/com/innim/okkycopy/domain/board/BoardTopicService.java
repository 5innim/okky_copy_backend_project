package com.innim.okkycopy.domain.board;

import com.innim.okkycopy.domain.board.dto.response.topics.TopicListResponse;
import com.innim.okkycopy.domain.board.entity.BoardType;
import com.innim.okkycopy.domain.board.repository.BoardTypeRepository;
import com.innim.okkycopy.global.error.ErrorCase;
import com.innim.okkycopy.global.error.exception.StatusCode500Exception;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BoardTopicService {

    private final BoardTypeRepository boardTypeRepository;

    @Transactional(readOnly = true)
    public TopicListResponse findBoardTopics() {
        List<BoardType> boardTypes = boardTypeRepository.findAll();

        if (boardTypes.isEmpty()) {
            throw new StatusCode500Exception(ErrorCase._500_FAIL_INITIALIZATION);
        }

        return TopicListResponse.from(boardTypes);
    }

}
