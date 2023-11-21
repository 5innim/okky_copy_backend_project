package com.innim.okkycopy.domain.member.entity;

import com.innim.okkycopy.domain.board.entity.Post;
import com.innim.okkycopy.domain.member.dto.request.SignupRequest;
import com.innim.okkycopy.global.auth.enums.Role;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Table(name = "member")
@DynamicUpdate
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Member {

    @Id
    @Column(name = "member_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long memberId;

    @Column(nullable = false, unique = true)
    private String id;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String email;

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

    @Column(name = "login_date")
    private LocalDateTime loginDate;

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY)
    private List<Post> posts;

    public static Member toUserEntity(SignupRequest request) {
        return Member.builder()
            .id(request.getId())
            .password(request.getPassword())
            .email(request.getEmail())
            .name(request.getName())
            .nickname(request.getNickname())
            .emailCheck(request.isEmailCheck())
            .role(Role.USER)
            .build();
    }

}
