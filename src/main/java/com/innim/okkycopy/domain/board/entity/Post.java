package com.innim.okkycopy.domain.board.entity;

import com.innim.okkycopy.domain.board.comment.entity.Comment;
import com.innim.okkycopy.domain.member.entity.Member;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Table(name = "post")
@Setter
@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "dtype")
@DynamicUpdate
public class Post {

    @Id
    @Column(name = "post_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId;
    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;
    @Column(nullable = false)
    private String title;
    @Column(length = 20000, nullable = false)
    private String content;
    @ManyToOne(optional = false)
    @JoinColumn(name = "topic_id")
    private BoardTopic boardTopic;
    @Column(nullable = false)
    private Integer likes;
    @Column(nullable = false)
    private Integer hates;
    @Column(nullable = false)
    private Integer scraps;
    @Column(nullable = false)
    private Integer views;
    @Column(nullable = false)
    private Integer comments;
    @CreationTimestamp
    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;
    @Column(name = "last_update")
    private LocalDateTime lastUpdate;
    @BatchSize(size = 100)
    @OneToMany(mappedBy = "post", cascade = {CascadeType.REMOVE, CascadeType.PERSIST}, orphanRemoval = true)
    private List<Tag> tags;
    @OneToMany(mappedBy = "post", cascade = {CascadeType.REMOVE})
    private List<Scrap> scrapList;
    @OneToMany(mappedBy = "post", cascade = {CascadeType.REMOVE})
    private List<PostExpression> postExpressionList;
    @OrderBy("createdDate ASC")
    @OneToMany(mappedBy = "post", cascade = {CascadeType.REMOVE})
    private List<Comment> commentList;


    public void remove(EntityManager entityManager) {
        entityManager.remove(this);
    }

    public void increaseLikes() {
        this.setLikes(this.getLikes() + 1);
    }

    public void decreaseLikes() {
        this.setLikes(this.getLikes() - 1);
    }

    public void increaseHates() {
        this.setHates(this.getHates() + 1);
    }

    public void decreaseHates() {
        this.setHates(this.getHates() - 1);
    }

    public void increaseScraps() {
        this.setScraps(this.getScraps() + 1);
    }

    public void decreaseScraps() {
        this.setScraps(this.getScraps() - 1);
    }

    public void increaseViews() {
        this.setViews(this.getViews() + 1);
    }
}
