package com.suyash.library.service;

import com.suyash.library.dto.AuthResponse;
import com.suyash.library.dto.LoginRequest;
import com.suyash.library.dto.RegisterRequest;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
}
