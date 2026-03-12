package com.app.cms.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class LoginResponse {
    private Map<String,String> tokens;
    private String type = "Bearer";
    private UserDto user;
    private OrganizationDto organization;

    public LoginResponse(Map<String,String> tokens, UserDto user, OrganizationDto organization) {
        this.tokens = tokens;
        this.user = user;
        this.organization = organization;
    }
}
