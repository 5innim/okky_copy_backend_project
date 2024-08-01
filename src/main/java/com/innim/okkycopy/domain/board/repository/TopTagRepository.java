package com.innim.okkycopy.domain.board.repository;

import com.innim.okkycopy.domain.board.entity.TopTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface TopTagRepository extends JpaRepository<TopTag, Integer> {

    @Modifying
    @Query("DELETE FROM TopTag")
    void deleteAll();
}
