package com.innim.okkycopy.domain.board.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tag_weekly_stats")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Setter
@Getter
public class TagWeeklyStats {

    @Id
    String name;
    @Column(nullable = false)
    Integer creates;

}
