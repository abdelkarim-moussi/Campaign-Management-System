package com.app.cms.channel.controller;

import com.app.cms.channel.service.ChannelService;
import com.app.cms.channel.entity.MessageSent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/channels")
@RequiredArgsConstructor
@Slf4j
public class ChannelController {
    private final ChannelService channelService;

    @GetMapping("/messages/campaign/{campaignId}")
    public ResponseEntity<Page<MessageSent>> getMessagesByCampaign(@PathVariable Long campaignId, Pageable pageable) {
        return ResponseEntity.ok(channelService.getMessagesByCampaign(campaignId, pageable));
    }

    @GetMapping("/messages/contact/{contactId}")
    public ResponseEntity<Page<MessageSent>> getMessagesByContact(@PathVariable Long contactId, Pageable pageable) {
        return ResponseEntity.ok(channelService.getMessagesByContact(contactId, pageable));
    }

    @GetMapping("/messages/{id}")
    public ResponseEntity<MessageSent> getMessage(@PathVariable Long id) {
        return ResponseEntity.ok(channelService.getMessage(id));
    }
}
