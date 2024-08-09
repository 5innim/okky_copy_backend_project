package com.innim.okkycopy.domain.board.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "tag")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Setter
@Getter
@Builder
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tag_id")
    private Long tagId;
    @ManyToOne(optional = false)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;
    @Column(nullable = false)
    private String name;
    @CreationTimestamp
    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id", nullable = false)
    private BoardTopic boardTopic;

    public static Tag of(Post post, BoardTopic boardTopic, String name) {
        return Tag.builder()
            .post(post)
            .name(name)
            .boardTopic(boardTopic)
            .build();
    }
}
