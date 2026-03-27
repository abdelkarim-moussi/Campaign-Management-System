package com.app.cms.channel.service;

import com.app.cms.channel.dto.EmailDto;
import com.app.cms.channel.dto.SendResult;
import com.app.cms.channel.dto.SmsDto;
import com.app.cms.channel.entity.MessageSent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface ChannelService {
    SendResult sendEmail(EmailDto dto);
    SendResult sendSms(SmsDto dto);
    MessageSent getMessage(Long id);
    Page<MessageSent> getMessagesByCampaign(Long campaignId, Pageable pageable);
    Page<MessageSent> getMessagesByContact(Long contactId, Pageable pageable);
}
