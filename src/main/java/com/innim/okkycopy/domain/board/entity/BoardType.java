package com.innim.okkycopy.domain.board.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "board_type")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BoardType {
    @Id
    @Column(name = "type_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long typeId;

    @OrderBy("type_id ASC")
    @OneToMany(mappedBy = "boardType")
    private List<BoardTopic> boardTopics;

    @Column(nullable = false, unique = true)
    private String name;




}

