package com.app.cms.channel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SmsDto {
    private Long campaignId;
    private Long contactId;
    private String to;
    private String content;
    private String from;
}
