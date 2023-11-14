package com.innim.okkycopy.domain.board.repository;

import com.innim.okkycopy.domain.board.entity.BoardType;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardTypeRepository extends JpaRepository<BoardType, Long> {
    List<BoardType> findAll();

}
