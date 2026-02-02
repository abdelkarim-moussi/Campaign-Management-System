package com.app.cms.contact;

import com.app.cms.contact.domain.Contact;
import com.app.cms.contact.domain.ContactStatus;

import java.util.List;

public interface ContactService {
    public Contact createContact(ContactDto dto);
    public Contact getContact(Long id);
    public List<Contact> getAllContacts();
    public List<Contact> getContactsByStatus(ContactStatus status);
    public List<Contact> searchContacts(String keyword);
    public Contact updateContact(Long id, ContactDto dto);
    public void deleteContact(Long id);
    public List<Contact> getContactsByIds(List<Long> ids);
}
