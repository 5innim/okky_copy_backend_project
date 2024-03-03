package com.innim.okkycopy.domain.board.entity;

import com.innim.okkycopy.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Table(name = "scrap", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"member_id", "post_id"})})
@DynamicUpdate
@DynamicInsert
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
public class Scrap {

    @Id
    @Column(name = "scrap_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long scrapId;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    public static Scrap createScrap(Post post, Member member) {
        post.increaseScraps();
        return Scrap.builder()
                .member(member)
                .post(post)
                .build();
    }

    public static void removeScrap(EntityManager entityManager, Scrap scrap) {
        scrap.getPost().decreaseScraps();
        entityManager.remove(scrap);
    }

}
