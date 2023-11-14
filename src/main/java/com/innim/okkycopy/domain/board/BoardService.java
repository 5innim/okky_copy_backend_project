package com.innim.okkycopy.domain.board;

import com.innim.okkycopy.domain.board.dto.response.topics.TopicsResponse;
import com.innim.okkycopy.domain.board.entity.BoardType;
import com.innim.okkycopy.domain.board.repository.BoardTypeRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardTypeRepository boardTypeRepository;

    public TopicsResponse findAllBoardTypes() {
        List<BoardType> boardTypes = boardTypeRepository.findAll();
        return TopicsResponse.toDto(boardTypes);
    }

}
