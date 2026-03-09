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

    @PostMapping("/webhook/postmark")
    public ResponseEntity<Void> handlePostmarkWebhook(@RequestBody Map<String, Object> event) {
        log.info("Received Postmark webhook");

        String recordType = (String) event.get("RecordType");
        String messageId = (String) event.get("MessageID");

        log.info("Postmark event: {} for message: {}", recordType, messageId);

        MessageStatus newStatus = switch (recordType) {
            case "Delivery" -> MessageStatus.DELIVERED;
            case "Open" -> MessageStatus.OPENED;
            case "Click" -> MessageStatus.CLICKED;
            case "Bounce" -> {
                String bounceType = (String) event.get("Type");
                // HardBounce ou SoftBounce
                yield MessageStatus.BOUNCED;
            }
            case "SpamComplaint" -> MessageStatus.FAILED;
            default -> null;
        };

        if (newStatus != null && messageId != null) {
            channelService.updateMessageStatus(messageId, newStatus);
        }

        return ResponseEntity.ok().build();
    }


    @PostMapping("/webhook/twilio")
    public ResponseEntity<Void> handleTwilioWebhook(@RequestParam Map<String, String> params) {
        log.info("Received Twilio webhook");

        String messageSid = params.get("MessageSid");
        String messageStatus = params.get("MessageStatus");

        log.info("Twilio status: {} for SID: {}", messageStatus, messageSid);

        MessageStatus newStatus = switch (messageStatus) {
            case "delivered" -> MessageStatus.DELIVERED;
            case "failed" -> MessageStatus.FAILED;
            case "undelivered" -> MessageStatus.FAILED;
            default -> null;
        };

        if (newStatus != null) {
            channelService.updateMessageStatus(messageSid, newStatus);
        }

        return ResponseEntity.ok().build();
    }
}
