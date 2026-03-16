package com.app.cms.user.sevice;

import com.app.cms.user.dto.*;

public interface AuthService {
    public LoginResponse register(RegisterRequest request);
    LoginResponse login(LoginRequest request);
    LoginResponse acceptInvitation(AcceptInvitationRequest request);
    UserDto getCurrentUser();
    RefreshTokenResponse refreshToken(RefreshTokenRequest request);
}
