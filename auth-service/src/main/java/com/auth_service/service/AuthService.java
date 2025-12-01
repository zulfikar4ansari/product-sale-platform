package com.auth_service.service;

import com.auth_service.dto.JwtResponse;
import com.auth_service.dto.LoginRequest;
import com.auth_service.dto.RegisterRequest;


public interface AuthService {

    void register(RegisterRequest request);

    JwtResponse login(LoginRequest request);
}
