package com.app.cms.contact;

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
import java.util.stream.Collectors;

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
}
