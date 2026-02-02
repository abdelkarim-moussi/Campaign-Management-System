package com.app.cms.contact;

import com.app.cms.common.NotFoundException;
import com.app.cms.contact.domain.Contact;
import com.app.cms.contact.domain.ContactStatus;
import com.app.cms.contact.domain.Tag;
import com.app.cms.contact.event.ContactCreatedEvent;
import com.app.cms.contact.exception.EmailAlreadyExistException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j

public class ContactServiceImpl implements ContactService{
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

        Contact contact = Contact.builder()
                        .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .company(dto.getCompany())
                .status(dto.getStatus() != null ? dto.getStatus() : ContactStatus.LEAD)
        .build();

        if (dto.getTagIds() != null && !dto.getTagIds().isEmpty()) {
            Set<Tag> tags = new HashSet<>(tagRepository.findAllById(dto.getTagIds()));
            contact.setTags(tags);
        }

        Contact savedContact = contactRepository.save(contact);

        eventPublisher.publishEvent(new ContactCreatedEvent(savedContact.getId()));

        log.info("Contact created with ID: {}", savedContact.getId());
        return savedContact;
    }

    @Override
    public Contact getContact(Long id) {
        return contactRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("contact not found"));
    }

    @Override
    public List<Contact> getAllContacts() {
        return contactRepository.findAll();
    }

    @Override
    public List<Contact> getContactsByStatus(ContactStatus status) {
        return contactRepository.findByStatus(status);
    }

    @Override
    public List<Contact> searchContacts(String keyword) {
        return List.of();
    }

    @Transactional
    @Override
    public Contact updateContact(Long id, ContactDto dto) {
        log.info("Updating contact: {}", id);

        Contact contact = Contact.builder()
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .company(dto.getCompany())
                .status(dto.getStatus())
                .build();

        if (dto.getTagIds() != null) {
            Set<Tag> tags = new HashSet<>(tagRepository.findAllById(dto.getTagIds()));
            contact.setTags(tags);
        }

        return contactRepository.save(contact);
    }

    @Transactional
    @Override
    public void deleteContact(Long id) {
        log.info("Deleting contact: {}", id);

        if (!contactRepository.existsById(id)) {
            throw new NotFoundException("Contact not found: " + id);
        }

        contactRepository.deleteById(id);
    }

    @Override
    public List<Contact> getContactsByIds(List<Long> ids) {
        return contactRepository.findAllById(ids);
    }


}
