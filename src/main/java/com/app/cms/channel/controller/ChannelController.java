package com.app.cms.channel.controller;

import com.app.cms.channel.service.ChannelService;
import com.app.cms.channel.entity.MessageSent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
