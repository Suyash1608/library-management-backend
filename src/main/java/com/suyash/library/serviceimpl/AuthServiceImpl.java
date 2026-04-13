package com.suyash.library.serviceimpl;

import com.suyash.library.dto.AuthResponse;
import com.suyash.library.dto.LoginRequest;
import com.suyash.library.dto.RegisterRequest;
import com.suyash.library.model.Member;
import com.suyash.library.model.MemberRole;
import com.suyash.library.repository.MemberRepository;
import com.suyash.library.security.JwtUtil;
import com.suyash.library.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;

    @Override
    public AuthResponse register(RegisterRequest request) {
        if (memberRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already registered: " + request.getEmail());
        }

        Member member = Member.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .role(MemberRole.MEMBER)
                .active(true)
                .build();

        memberRepository.save(member);

        UserDetails userDetails = userDetailsService.loadUserByUsername(member.getEmail());
        String token = jwtUtil.generateToken(userDetails);

        return AuthResponse.builder()
                .token(token)
                .email(member.getEmail())
                .name(member.getName())
                .role(member.getRole())
                .build();
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        Member member = memberRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        UserDetails userDetails = userDetailsService.loadUserByUsername(member.getEmail());
        String token = jwtUtil.generateToken(userDetails);

        return AuthResponse.builder()
                .token(token)
                .email(member.getEmail())
                .name(member.getName())
                .role(member.getRole())
                .build();
    }
}
