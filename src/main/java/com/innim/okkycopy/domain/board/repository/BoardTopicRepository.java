package com.innim.okkycopy.domain.board.repository;

import com.innim.okkycopy.domain.board.entity.BoardTopic;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;


public interface BoardTopicRepository extends JpaRepository<BoardTopic, Long> {
    Optional<BoardTopic> findByName(String name);
}
