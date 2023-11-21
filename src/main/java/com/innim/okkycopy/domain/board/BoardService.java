package com.innim.okkycopy.domain.board;

import com.innim.okkycopy.domain.board.dto.response.topics.TopicsResponse;
import com.innim.okkycopy.domain.board.entity.BoardType;
import com.innim.okkycopy.domain.board.repository.BoardTypeRepository;
import com.innim.okkycopy.global.error.ErrorCode;
import com.innim.okkycopy.global.error.exception.FailInitializationException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardTypeRepository boardTypeRepository;

    public TopicsResponse findAllBoardTopics() {
        List<BoardType> boardTypes = boardTypeRepository.findAll();

        if (boardTypes.isEmpty())
            throw new FailInitializationException(ErrorCode._500_FAIL_INITIALIZATION);

        return TopicsResponse.toDto(boardTypes);
    }

}
