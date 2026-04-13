package com.suyash.library.dto;

import com.suyash.library.model.MemberRole;
import lombok.*;
import java.time.LocalDateTime;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class MemberResponse {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private MemberRole role;
    private Boolean active;
    private LocalDateTime memberSince;
    private int activeBorrows;
}
