package com.app.cms.channel;

import java.util.List;

public interface ChannelService {
    SendResult sendEmail(EmailDto dto);
    SendResult sendSms(SmsDto dto);
    MessageSent getMessage(Long id);
    List<MessageSent> getMessagesByCampaign(Long campaignId);
    List<MessageSent> getMessagesByContact(Long contactId);
    void updateMessageStatus(String externalId, MessageStatus newStatus);
}
