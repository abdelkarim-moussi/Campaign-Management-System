package com.app.cms.channel.service;

import com.app.cms.channel.dto.EmailDto;
import com.app.cms.channel.dto.SendResult;
import com.app.cms.channel.dto.SmsDto;
import com.app.cms.channel.entity.MessageSent;
import com.app.cms.channel.entity.MessageStatus;

import java.util.List;

public interface ChannelService {
    SendResult sendEmail(EmailDto dto);
    SendResult sendSms(SmsDto dto);
    MessageSent getMessage(Long id);
    List<MessageSent> getMessagesByCampaign(Long campaignId);
    List<MessageSent> getMessagesByContact(Long contactId);
    void updateMessageStatus(String externalId, MessageStatus newStatus);
}
