package com.app.cms.contact;

import com.app.cms.contact.domain.ContactEntity;
import com.app.cms.contact.domain.ContactStatus;

import java.util.List;

public interface ContactService {
    public ContactEntity createContact(ContactDto dto);
    public ContactEntity getContact(Long id);
    public List<ContactEntity> getAllContacts();
    public List<ContactEntity> getContactsByStatus(ContactStatus status);
    public List<ContactEntity> searchContacts(String keyword);
    public ContactEntity updateContact(Long id, ContactDto dto);
    public void deleteContact(Long id);
    public List<ContactEntity> getContactsByIds(List<Long> ids);
}
