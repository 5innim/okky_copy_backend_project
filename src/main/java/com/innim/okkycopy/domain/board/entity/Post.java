package com.innim.okkycopy.domain.board.entity;

import com.innim.okkycopy.domain.board.comment.entity.Comment;
import com.innim.okkycopy.domain.board.knowledge.entity.KnowledgePost;
import com.innim.okkycopy.domain.member.entity.Member;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Table(name = "post")
@Setter
@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Inheritance(strategy = InheritanceType.JOINED)
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
    @CreationTimestamp
    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;
    @Column(name = "last_update")
    private LocalDateTime lastUpdate;
    @OneToMany(mappedBy = "post", cascade = {CascadeType.REMOVE, CascadeType.PERSIST}, orphanRemoval = true)
    private List<Tag> tags;
    @OneToMany(mappedBy = "post", cascade = {CascadeType.REMOVE})
    private List<Scrap> scrapList;
    @OneToMany(mappedBy = "post", cascade = {CascadeType.REMOVE})
    private List<PostExpression> postExpressionList;
    @OneToMany(mappedBy = "post", cascade = {CascadeType.REMOVE})
    private List<Comment> commentList;

    public void increaseLikes() {
        if (this instanceof KnowledgePost)
            ((KnowledgePost) this).setLikes(((KnowledgePost) this).getLikes() + 1);
    }

    public void decreaseLikes() {
        if (this instanceof KnowledgePost)
            ((KnowledgePost) this).setLikes(((KnowledgePost) this).getLikes() - 1);
    }

    public void increaseHates() {
        if (this instanceof KnowledgePost)
            ((KnowledgePost) this).setHates(((KnowledgePost) this).getHates() + 1);
    }

    public void decreaseHates() {
        if (this instanceof KnowledgePost)
            ((KnowledgePost) this).setHates(((KnowledgePost) this).getHates() - 1);
    }

    public void increaseScraps() {
        if (this instanceof KnowledgePost)
            ((KnowledgePost) this).setScraps(((KnowledgePost) this).getScraps() - 1);
    }

    public void decreaseScraps() {
        if (this instanceof KnowledgePost)
            ((KnowledgePost) this).setScraps(((KnowledgePost) this).getScraps() - 1);
    }
}
