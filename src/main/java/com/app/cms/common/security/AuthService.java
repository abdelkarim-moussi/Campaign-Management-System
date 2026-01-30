package com.app.cms.common.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;

    public AuthResponse authenticate(AuthRequest authRequest){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authRequest.getEmail(),authRequest.getPassword())
        );

        if(authentication.isAuthenticated()){
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            Map<String,String> tokens = jwtService.generateTokenPair(userDetails);
            return AuthResponse.builder()
                    .accessToken(tokens.get("accessToken"))
                    .refreshToken(tokens.get("refreshToken"))
                    .build();
        }else {
            throw new UsernameNotFoundException("Invalid user request");
        }
    }

}
