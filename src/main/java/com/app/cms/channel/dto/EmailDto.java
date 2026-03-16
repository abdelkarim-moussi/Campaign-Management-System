package com.app.cms.channel.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmailDto {
    private Long campaignId;
    private Long contactId;
    private String to;
    private String subject;
    private String content;
    private String fromName;
    private String fromEmail;
}
