package com.app.cms.user.dto;

import lombok.Data;

@Data
public class UpdateProfileRequest {
    private String firstName;
    private String lastName;
    private String timezone;
    private String language;
}
