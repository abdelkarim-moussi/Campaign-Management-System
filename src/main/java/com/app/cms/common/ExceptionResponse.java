package com.app.cms.common;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@Builder
public class ExceptionResponse {
    int status;
    String message;
    String error;
    String path;
    LocalDateTime timestamp = LocalDateTime.now();
}
