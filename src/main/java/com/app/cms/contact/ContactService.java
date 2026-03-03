package com.app.cms.contact;

import java.util.List;

public interface ContactService {
    Contact createContact(ContactDto dto);
    List<Contact> importContacts(List<ContactDto> dtos);
    Contact getContact(Long id);
    List<Contact> getAllContacts();
    List<Contact> getContactsByStatus(ContactStatus status);
    List<Contact> searchContacts(String keyword);
    Contact updateContact(Long id, ContactDto dto);
    void deleteContact(Long id);
    List<Contact> getContactsByIds(List<Long> ids);
}
