package com.app.cms.common.security;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> authenticateAndGetToken(@RequestBody @Valid AuthRequest authRequest){
        return ResponseEntity.ok(authService.authenticate(authRequest));
    }

    @PostMapping("/initUser")
    public ResponseEntity<UserEntity> initUser(){
        return ResponseEntity.ok(authService.initUser());
    }

}
