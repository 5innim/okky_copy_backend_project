package com.innim.okkycopy.domain.member.entity;

import com.innim.okkycopy.domain.board.comment.entity.Comment;
import com.innim.okkycopy.domain.board.comment.entity.CommentExpression;
import com.innim.okkycopy.domain.board.entity.Post;
import com.innim.okkycopy.domain.board.entity.PostExpression;
import com.innim.okkycopy.domain.board.entity.Scrap;
import com.innim.okkycopy.global.auth.enums.Role;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
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
import org.hibernate.annotations.DynamicInsert;


@Entity
@Table(name = "member")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "dtype")
@SuperBuilder
@Getter
@Setter
@DynamicInsert
public class Member {

    @Id
    @Column(name = "member_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String nickname;

    @Column(nullable = false, name = "email_check")
    private boolean emailCheck;

    @Column(nullable = false, name = "created_date")
    @CreationTimestamp
    private LocalDateTime createdDate;

    @Enumerated(EnumType.ORDINAL)
    @Column(nullable = false)
    private Role role;

    @Column
    private String profile;

    @Column(name = "login_date")
    private LocalDateTime loginDate;

    @Column(name = "logout_date")
    private LocalDateTime logoutDate;

    @OneToMany(mappedBy = "member")
    private List<Post> posts;

    @OneToMany(mappedBy = "member", cascade = {CascadeType.REMOVE})
    private List<Scrap> scrapList;

    @OneToMany(mappedBy = "member", cascade = {CascadeType.REMOVE})
    private List<PostExpression> postExpressionList;

    @OneToMany(mappedBy = "member", cascade = {CascadeType.REMOVE})
    private List<CommentExpression> commentExpressionList;

    @OneToMany(mappedBy = "member")
    private List<Comment> comments;


    //TODO "if new SNS type is added, then should expand logic
    public String findEmail() {
        if (this instanceof OkkyMember) {
            return ((OkkyMember) this).getEmail();
        } else if (this instanceof GoogleMember) {
            return ((GoogleMember) this).getEmail();
        } else if (this instanceof NaverMember) {
            return ((NaverMember) this).getEmail();
        } else if (this instanceof KakaoMember) {
            return ((KakaoMember) this).getEmail();
        }

        return "";
    }

    public void changeEmail(String email) {
        if (this instanceof OkkyMember) {
            ((OkkyMember) this).setEmail(email);
        } else if (this instanceof GoogleMember) {
            ((GoogleMember) this).setEmail(email);
        } else if (this instanceof NaverMember) {
            ((NaverMember) this).setEmail(email);
        } else if (this instanceof KakaoMember) {
            ((KakaoMember) this).setEmail(email);
        }
    }

    public void remove(EntityManager entityManager) {
        Member member = entityManager.merge(this);

        for (Post post : member.getPosts()) {
            post.setMember(null);
        }

        for (Comment comment : member.getComments()) {
            comment.setMember(null);
        }

        entityManager.remove(member);
    }


}
