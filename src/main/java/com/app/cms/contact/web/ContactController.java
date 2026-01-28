package com.app.cms.contact.web;

import com.app.cms.contact.ContactDto;
import com.app.cms.contact.ContactService;
import com.app.cms.contact.domain.ContactEntity;
import com.app.cms.contact.domain.ContactStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contacts")
@RequiredArgsConstructor
public class ContactController {

    private final ContactService contactService;

    @PostMapping
    public ResponseEntity<ContactEntity> createContact(@Valid @RequestBody ContactDto dto) {
        ContactEntity contact = contactService.createContact(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(contact);
    }

    @GetMapping
    public ResponseEntity<List<ContactEntity>> getAllContacts() {
        return ResponseEntity.ok(contactService.getAllContacts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContactEntity> getContact(@PathVariable Long id) {
        return ResponseEntity.ok(contactService.getContact(id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ContactEntity>> searchContacts(@RequestParam String keyword) {
        return ResponseEntity.ok(contactService.searchContacts(keyword));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<ContactEntity>> getContactsByStatus(@PathVariable ContactStatus status) {
        return ResponseEntity.ok(contactService.getContactsByStatus(status));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ContactEntity> updateContact(
            @PathVariable Long id,
            @Valid @RequestBody ContactDto dto) {
        return ResponseEntity.ok(contactService.updateContact(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContact(@PathVariable Long id) {
        contactService.deleteContact(id);
        return ResponseEntity.noContent().build();
    }
}