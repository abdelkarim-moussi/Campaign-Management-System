package com.app.cms.contact;

import com.app.cms.common.NotFoundException;
import com.app.cms.common.security.OrganizationContext;
import com.app.cms.contact.event.ContactCreatedEvent;
import com.app.cms.contact.exception.EmailAlreadyExistException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j

public class ContactServiceImpl implements ContactService {
    private final ContactRepository contactRepository;
    private final TagRepository tagRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    @Override
    public Contact createContact(ContactDto dto) {
        log.info("Creating contact: {}", dto.getEmail());

        if (contactRepository.existsByEmail(dto.getEmail())) {
            throw new EmailAlreadyExistException("Email already exists: " + dto.getEmail());
        }

        Contact savedContact = contactRepository.save(processContact(dto));

        eventPublisher.publishEvent(new ContactCreatedEvent(savedContact.getId()));

        log.info("Contact created with ID: {}", savedContact.getId());
        return savedContact;
    }

    @Transactional
    @Override
    public List<Contact> importContacts(List<ContactDto> dtos) {
        log.info("Importing contacts");

        List<Contact> contacts = new ArrayList<>();

        dtos.forEach(dto -> {
            if (contactRepository.existsByEmail(dto.getEmail())) {
                dtos.remove(dto);
            }
            ;

            contacts.add(processContact(dto));

        });

        List<Contact> savedContacts = contactRepository.saveAll(contacts);

        // eventPublisher.publishEvent(new ContactsImportedEvent());

        log.info("Contacts Imported");
        return savedContacts;
    }

    private Contact processContact(ContactDto dto) {
        Long organizationId = OrganizationContext.getOrganizationId();

        Contact contact = Contact.builder()
                .organizationId(organizationId)
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .company(dto.getCompany())
                .status(dto.getStatus() != null ? dto.getStatus() : ContactStatus.LEAD)
                .build();

        if (dto.getTagIds() != null && !dto.getTagIds().isEmpty()) {
            Set<Tag> tags = new HashSet<>(tagRepository.findByIdInAndOrganizationId(dto.getTagIds(), organizationId));
            contact.setTags(tags);
        }

        return contact;
    }

    @Override
    public Contact getContact(Long contactId) {
        Long organizationId = OrganizationContext.getOrganizationId();
        return contactRepository.findByIdAndOrganization(contactId, organizationId)
                .orElseThrow(() -> new NotFoundException("contact not found"));
    }

    @Override
    public List<Contact> getAllContacts() {
        Long organizationId = OrganizationContext.getOrganizationId();
        return contactRepository.findAllByOrganization(organizationId);
    }

    @Override
    public List<Contact> getContactsByStatus(ContactStatus status) {
        Long organizationId = OrganizationContext.getOrganizationId();
        return contactRepository.findByStatusAndOrganization(status, organizationId);
    }

    @Override
    public List<Contact> searchContacts(String keyword) {
        Long organizationId = OrganizationContext.getOrganizationId();
        return contactRepository.searchContactsByOrganization(keyword, organizationId);
    }

    @Transactional
    @Override
    public Contact updateContact(Long id, ContactDto dto) {
        log.info("Updating contact: {}", id);

        Long organizationId = OrganizationContext.getOrganizationId();

        Contact contact = contactRepository.findByIdAndOrganization(id, organizationId)
                .orElseThrow(() -> new NotFoundException("Contact Not Found With id : " + id));

        contact.setFirstName(dto.getFirstName());
        contact.setLastName(dto.getLastName());
        contact.setEmail(dto.getEmail());
        contact.setPhone(dto.getPhone());
        contact.setStatus(dto.getStatus());
        contact.setCompany(dto.getCompany());

        if (dto.getTagIds() != null) {
            Set<Tag> tags = new HashSet<>(tagRepository.findByIdInAndOrganizationId(dto.getTagIds(), organizationId));
            contact.setTags(tags);
        }

        return contactRepository.save(contact);
    }

    @Transactional
    @Override
    public void deleteContact(Long id) {
        log.info("Deleting contact: {}", id);

        Long organizationId = OrganizationContext.getOrganizationId();

        if (!contactRepository.existsByIdAndOrganization(id, organizationId)) {
            throw new NotFoundException("Contact not found: " + id);
        }

        contactRepository.deleteByIdAndOrganizationId(id, organizationId);
    }

    @Override
    public List<Contact> getContactsByIds(List<Long> ids) {
        Long organizationId = OrganizationContext.getOrganizationId();
        return contactRepository.findByIdInAndOrganization(ids, organizationId);
    }

}
