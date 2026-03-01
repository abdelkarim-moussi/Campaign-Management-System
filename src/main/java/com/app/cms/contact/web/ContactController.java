package com.app.cms.contact.web;

import com.app.cms.contact.ContactDto;
import com.app.cms.contact.ContactService;
import com.app.cms.contact.domain.Contact;
import com.app.cms.contact.domain.ContactStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/contacts")
@RequiredArgsConstructor
public class ContactController {

    private final ContactService contactService;

    @PostMapping
    public ResponseEntity<Contact> createContact(@Valid @RequestBody ContactDto dto) {
        Contact contact = contactService.createContact(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(contact);
    }

    @PostMapping("/import")
    public ResponseEntity<List<Contact>> importContacts(@Valid @RequestBody List<ContactDto> dtos) {
        List<Contact> contacts = contactService.importContacts(dtos);
        return ResponseEntity.status(HttpStatus.CREATED).body(contacts);
    }

    @GetMapping
    public ResponseEntity<List<Contact>> getAllContacts() {
        return ResponseEntity.ok(contactService.getAllContacts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Contact> getContact(@PathVariable Long id) {
        return ResponseEntity.ok(contactService.getContact(id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<Contact>> searchContacts(@RequestParam String keyword) {
        return ResponseEntity.ok(contactService.searchContacts(keyword));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Contact>> getContactsByStatus(@PathVariable ContactStatus status) {
        return ResponseEntity.ok(contactService.getContactsByStatus(status));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Contact> updateContact(
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