package com.innim.okkycopy.unit.board.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.innim.okkycopy.domain.board.dto.response.topics.TopicListResponse;
import com.innim.okkycopy.domain.board.entity.BoardType;
import com.innim.okkycopy.domain.board.repository.BoardTypeRepository;
import com.innim.okkycopy.domain.board.service.BoardTopicService;
import com.innim.okkycopy.global.error.ErrorCase;
import com.innim.okkycopy.global.error.exception.StatusCode500Exception;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class BoardTopicServiceTest {

    @Mock
    BoardTypeRepository boardTypeRepository;
    @InjectMocks
    BoardTopicService boardTopicService;

    @Nested
    class _findBoardTopics {

        @Test
        void given_boardTypeIsNotExist_then_throwErrorCase500002() {
            // given
            given(boardTypeRepository.findAll()).willReturn(Collections.emptyList());

            // when
            Exception exception = catchException(() -> {
                boardTopicService.findBoardTopics();
            });

            // then
            then(boardTypeRepository).should(times(1)).findAll();
            then(boardTypeRepository).shouldHaveNoMoreInteractions();
            assertThat(exception).isInstanceOf(StatusCode500Exception.class);
            assertThat(((StatusCode500Exception) exception).getErrorCase()).isEqualTo(
                ErrorCase._500_INITIALIZATION_FAIL);
        }

        @Test
        void given_boardTypeInitializationSuccess_then_returnTopicListResponse() {
            // given
            given(boardTypeRepository.findAll()).willReturn(boardTypes());

            // when
            TopicListResponse response = boardTopicService.findBoardTopics();

            // then
            then(boardTypeRepository).should(times(1)).findAll();
            then(boardTypeRepository).shouldHaveNoMoreInteractions();
            assertThat(response.getTypes().get(0).getName()).isEqualTo("test_board_type");
        }

        List<BoardType> boardTypes() {
            List<BoardType> boardTypes = new ArrayList<>();
            boardTypes.add(BoardType.builder()
                .typeId(1L)
                .name("test_board_type")
                .boardTopics(Collections.emptyList())
                .build());
            return boardTypes;
        }
    }

}
