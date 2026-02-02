package com.app.cms.common.security;

import jakarta.validation.constraints.NotBlank;

public record TokenRefreshRequest(
        @NotBlank(message = "Refresh Token is Required")
        String refreshToken) { }
