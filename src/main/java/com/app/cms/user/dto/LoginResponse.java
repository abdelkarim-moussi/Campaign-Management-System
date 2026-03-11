package com.app.cms.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private String type = "Bearer";
    private UserDto user;
    private OrganizationDto organization;

    public LoginResponse(String token, UserDto user, OrganizationDto organization) {
        this.token = token;
        this.user = user;
        this.organization = organization;
    }
}
