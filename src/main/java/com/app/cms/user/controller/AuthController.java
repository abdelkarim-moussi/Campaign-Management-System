package com.app.cms.user.web;

import com.app.cms.user.dto.*;
import com.app.cms.user.sevice.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<LoginResponse> register(@Valid @RequestBody RegisterRequest request) {
        LoginResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/accept-invitation")
    public ResponseEntity<LoginResponse> acceptInvitation(@Valid @RequestBody AcceptInvitationRequest request) {
        LoginResponse response = authService.acceptInvitation(request);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/me")
    public ResponseEntity<UserDto> getCurrentUser() {
        UserDto user = authService.getCurrentUser();
        return ResponseEntity.ok(user);
    }

}
