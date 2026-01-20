package com.app.cms.contact;

import com.app.cms.common.NotFoundException;
import com.app.cms.contact.domain.ContactEntity;
import com.app.cms.contact.domain.ContactStatus;
import com.app.cms.contact.domain.TagEntity;
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
    public ContactEntity createContact(ContactDto dto) {
        log.info("Creating contact: {}", dto.getEmail());

        if (contactRepository.existsByEmail(dto.getEmail())) {
            throw new EmailAlreadyExistException("Email already exists: " + dto.getEmail());
        }

        ContactEntity contact = ContactEntity.builder()
                        .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .company(dto.getCompany())
                .status(dto.getStatus() != null ? dto.getStatus() : ContactStatus.LEAD)
        .build();

        if (dto.getTagIds() != null && !dto.getTagIds().isEmpty()) {
            Set<TagEntity> tags = new HashSet<>(tagRepository.findAllById(dto.getTagIds()));
            contact.setTags(tags);
        }

        ContactEntity savedContact = contactRepository.save(contact);

        eventPublisher.publishEvent(new ContactCreatedEvent(savedContact.getId()));

        log.info("Contact created with ID: {}", savedContact.getId());
        return savedContact;
    }

    @Override
    public ContactEntity getContact(Long id) {
        return contactRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("contact not found"));
    }

    @Override
    public List<ContactEntity> getAllContacts() {
        return contactRepository.findAll();
    }

    @Override
    public List<ContactEntity> getContactsByStatus(ContactStatus status) {
        return contactRepository.findByStatus(status);
    }

    @Override
    public List<ContactEntity> searchContacts(String keyword) {
        return List.of();
    }

    @Override
    public ContactEntity updateContact(Long id, ContactDto dto) {
        return null;
    }

    @Override
    public void deleteContact(Long id) {

    }

    @Override
    public List<ContactEntity> getContactsByIds(List<Long> ids) {
        return List.of();
    }


}
