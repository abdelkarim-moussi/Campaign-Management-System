package com.app.cms.channel.web;

import com.app.cms.channel.ChannelService;
import com.app.cms.channel.MessageSent;
import com.app.cms.channel.MessageStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/channels")
@RequiredArgsConstructor
@Slf4j
public class ChannelController {
    private final ChannelService channelService;

    @GetMapping("/messages/campaign/{campaignId}")
    public ResponseEntity<List<MessageSent>> getMessagesByCampaign(@PathVariable Long campaignId) {
        return ResponseEntity.ok(channelService.getMessagesByCampaign(campaignId));
    }

    @GetMapping("/messages/contact/{contactId}")
    public ResponseEntity<List<MessageSent>> getMessagesByContact(@PathVariable Long contactId) {
        return ResponseEntity.ok(channelService.getMessagesByContact(contactId));
    }

    @GetMapping("/messages/{id}")
    public ResponseEntity<MessageSent> getMessage(@PathVariable Long id) {
        return ResponseEntity.ok(channelService.getMessage(id));
    }
}
