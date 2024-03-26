package com.innim.okkycopy.domain.board.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.innim.okkycopy.domain.board.entity.BoardTopic;
import com.innim.okkycopy.domain.board.entity.BoardType;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;


@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class BoardTopicRepositoryTest {

    @Autowired
    BoardTypeRepository boardTypeRepository;
    @Autowired
    BoardTopicRepository boardTopicRepository;


    /**
     * board_topic, board_type table will be initialized.
     */
    @Test
    void findByNameTest() {
        // given
        // execute procedure by data.sql
        List<BoardType> list = boardTypeRepository.findAll();
        BoardType type = list.get(0);
        BoardTopic topic = type.getBoardTopics().get(0);

        // when
        Optional<BoardTopic> optional = boardTopicRepository.findByName(topic.getName());

        // then
        assertThat(optional.isEmpty()).isFalse();

    }


}
