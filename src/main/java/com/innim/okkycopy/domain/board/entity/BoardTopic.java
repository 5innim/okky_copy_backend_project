package com.innim.okkycopy.domain.board.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Table(name = "board_topic")
@DynamicUpdate
public class BoardTopic {
    @Id
    @Column(name = "topic_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long topicId;
    @ManyToOne
    @JoinColumn(name = "type_id")
    private BoardType boardType;

    @Column(name = "name")
    private String name;



}
