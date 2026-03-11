package com.app.cms.user.sevice;

import com.app.cms.user.dto.InviteUserRequest;
import com.app.cms.user.dto.UserDto;
import com.app.cms.user.entity.Invitation;
import com.app.cms.user.entity.InvitationStatus;
import com.app.cms.user.entity.Organization;
import com.app.cms.user.entity.User;
import com.app.cms.user.mapper.UserMapper;
import com.app.cms.user.repository.InvitationRepository;
import com.app.cms.user.repository.OrganizationRepository;
import com.app.cms.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl {
    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;
    private final InvitationRepository invitationRepository;
    private final UserMapper userMapper;

    @Transactional
    public Invitation inviteUser(InviteUserRequest request) {
        Long organizationId = OrganizationContext.getOrganizationId();
        Long currentUserId = OrganizationContext.getUserId();

        log.info("Inviting user {} to organization {}", request.getEmail(), organizationId);

        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new RuntimeException("Current user not found"));

        if (!currentUser.canManageUsers()) {
            throw new SecurityException("You don't have permission to invite users");
        }

        Organization organization = currentUser.getOrganization();

        if (!organization.canAddUser()) {
            throw new IllegalArgumentException(
                    "Maximum users limit reached (" + organization.getMaxUsers() + ")");
        }


        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("User with this email already exists");
        }


        if (invitationRepository.existsByEmailAndOrganizationIdAndStatus(
                request.getEmail(), organizationId, InvitationStatus.PENDING)) {
            throw new IllegalArgumentException("Invitation already sent to this email");
        }


        Invitation invitation = new Invitation();
        invitation.setOrganization(organization);
        invitation.setEmail(request.getEmail());
        invitation.setRole(request.getRole());
        invitation.setInvitedBy(currentUser);

        Invitation saved = invitationRepository.save(invitation);

        log.info("Invitation created with token: {}", saved.getToken());
        log.info("TODO: Send invitation email to {}", request.getEmail());

        return saved;
    }

    public List<UserDto> getOrganizationUsers() {
        Long organizationId = OrganizationContext.getOrganizationId();

        return userRepository.findByOrganizationId(organizationId)
                .stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }
}
