package com.suyash.library.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "members")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberRole role;

    @Column(nullable = false)
    private Boolean active;

    @Column(updatable = false)
    private LocalDateTime memberSince;

    @PrePersist
    protected void onCreate() {
        memberSince = LocalDateTime.now();
        if (active == null) active = true;
        if (role == null) role = MemberRole.MEMBER;
    }
}
