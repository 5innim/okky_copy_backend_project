package com.innim.okkycopy.domain.board;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.assertj.core.api.Assertions.*;

import com.innim.okkycopy.domain.board.dto.response.topics.TopicsResponse;
import com.innim.okkycopy.domain.board.entity.BoardTopic;
import com.innim.okkycopy.domain.board.entity.BoardType;
import com.innim.okkycopy.domain.board.repository.BoardTypeRepository;
import com.innim.okkycopy.global.error.exception.FailInitializationException;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class BoardServiceTest {
    @Mock
    BoardTypeRepository boardTypeRepository;
    @InjectMocks
    BoardService boardService;

    @Nested
    class findAllBoardTopicsTest {
        @Test
        void findAllBoardTopics() {
            // given
            List<BoardType> boardTypes = boardTypes();
            given(boardTypeRepository.findAll()).willReturn(boardTypes);

            // when
            TopicsResponse topicsResponse = boardService.findAllBoardTopics();

            // then
            then(boardTypeRepository).should(times(1)).findAll();
            assertThat(topicsResponse.getTypes().get(0).getTopics().get(0).getName()).isEqualTo("test_topic");
        }

        @Test
        void given_emptyTypes_then_throwFailInitializationException() {
            // given
            given(boardTypeRepository.findAll()).willReturn(Arrays.asList());

            // when
            Throwable thrown = catchThrowable(()-> {
                boardService.findAllBoardTopics();
            });

            // then
            assertThat(thrown).isInstanceOf(FailInitializationException.class);
        }

        List<BoardType> boardTypes() {
            BoardType boardType = BoardType.builder()
                .typeId(1l)
                .name("test_type")
                .build();

            BoardTopic topic = BoardTopic.builder()
                .topicId(1l)
                .boardType(boardType)
                .name("test_topic")
                .build();

            boardType.setBoardTopics(Arrays.asList(topic));

            return Arrays.asList(boardType);
        }
    }




}
