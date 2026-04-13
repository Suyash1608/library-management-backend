package com.suyash.library.dto;

import com.suyash.library.model.MemberRole;
import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class AuthResponse {
    private String token;
    private String email;
    private String name;
    private MemberRole role;
}
