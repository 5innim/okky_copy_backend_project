package com.innim.okkycopy.domain.board.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.innim.okkycopy.domain.board.entity.BoardType;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class BoardTypeRepositoryTest {

    @Autowired
    BoardTypeRepository repository;

    /**
     * filndAllTest() method can't return empty list because board_topic, board_type table will be initialized.
     */
    @Test
    void findAllTest() {
        // given
        // execute procedure by data.sql

        // when
        List<BoardType> list = repository.findAll();

        // then
        assertThat(list).isNotEmpty();
    }
}
