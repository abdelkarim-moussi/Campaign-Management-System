package com.app.cms.contact.service;

import com.app.cms.contact.ContactDto;
import com.app.cms.contact.entity.ContactStatus;
import com.app.cms.contact.entity.Contact;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ContactService {
    Contact createContact(ContactDto dto);
    List<Contact> importContacts(List<ContactDto> dtos);
    Contact getContact(Long id);
    Page<Contact> getAllContacts(Pageable pageable);
    Page<Contact> getContactsByStatus(ContactStatus status, Pageable pageable);
    Page<Contact> searchContacts(String keyword, Pageable pageable);
    Contact updateContact(Long id, ContactDto dto);
    void deleteContact(Long id);
    List<Contact> getContactsByIds(List<Long> ids);
}
