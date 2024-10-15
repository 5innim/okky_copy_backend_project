package com.innim.okkycopy.domain.board.entity;

import com.innim.okkycopy.domain.member.entity.Member;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "scrap")
@IdClass(ScrapKey.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public class Scrap {

    @Id
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @Id
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    public static Scrap of(Post post, Member member) {
        post.increaseScraps();
        return Scrap.builder()
            .member(member)
            .post(post)
            .build();
    }

    public static void remove(EntityManager entityManager, Scrap scrap) {
        scrap.getPost().decreaseScraps();
        entityManager.remove(scrap);
    }

}
