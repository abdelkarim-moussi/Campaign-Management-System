package com.app.cms.contact.controller;

import com.app.cms.contact.ContactDto;
import com.app.cms.contact.service.ContactService;
import com.app.cms.contact.entity.Contact;
import com.app.cms.contact.entity.ContactStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    public ResponseEntity<Page<Contact>> getAllContacts(Pageable pageable) {
        return ResponseEntity.ok(contactService.getAllContacts(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Contact> getContact(@PathVariable Long id) {
        return ResponseEntity.ok(contactService.getContact(id));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<Contact>> searchContacts(@RequestParam String keyword, Pageable pageable) {
        return ResponseEntity.ok(contactService.searchContacts(keyword, pageable));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<Page<Contact>> getContactsByStatus(@PathVariable ContactStatus status, Pageable pageable) {
        return ResponseEntity.ok(contactService.getContactsByStatus(status, pageable));
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