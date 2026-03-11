package com.app.cms.user.sevice;

import com.app.cms.user.dto.InviteUserRequest;
import com.app.cms.user.dto.UpdateProfileRequest;
import com.app.cms.user.dto.UserDto;
import com.app.cms.user.entity.*;
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
public class UserServiceImpl implements UserService{
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

    @Transactional
    public UserDto updateProfile(UpdateProfileRequest request) {
        Long userId = OrganizationContext.getUserId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }
        if (request.getTimezone() != null) {
            user.setTimezone(request.getTimezone());
        }
        if (request.getLanguage() != null) {
            user.setLanguage(request.getLanguage());
        }

        User updated = userRepository.save(user);

        return userMapper.toDto(updated);
    }

    @Transactional
    public UserDto updateUserRole(Long userId, UserRole newRole) {
        Long organizationId = OrganizationContext.getOrganizationId();
        Long currentUserId = OrganizationContext.getUserId();

        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new RuntimeException("Current user not found"));

        if (!currentUser.canManageUsers()) {
            throw new SecurityException("You don't have permission to manage users");
        }

        User user = userRepository.findByIdAndOrganizationId(userId, organizationId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.isOwner()) {
            throw new IllegalArgumentException("Cannot modify owner role");
        }


        if (newRole == UserRole.ADMIN && !currentUser.isOwner()) {
            throw new SecurityException("Only owner can assign admin role");
        }

        user.setRole(newRole);
        User updated = userRepository.save(user);

        log.info("User {} role updated to {}", user.getEmail(), newRole);

        return userMapper.toDto(updated);
    }

    @Transactional
    public void deleteUser(Long userId) {
        Long organizationId = OrganizationContext.getOrganizationId();
        Long currentUserId = OrganizationContext.getUserId();

        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new RuntimeException("Current user not found"));

        if (!currentUser.canManageUsers()) {
            throw new SecurityException("You don't have permission to delete users");
        }

        User user = userRepository.findByIdAndOrganizationId(userId, organizationId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.isOwner()) {
            throw new IllegalArgumentException("Cannot delete owner");
        }


        if (user.getId().equals(currentUserId)) {
            throw new IllegalArgumentException("Cannot delete yourself");
        }

        userRepository.delete(user);

        Organization org = user.getOrganization();
        org.setCurrentUsers(org.getCurrentUsers() - 1);
        organizationRepository.save(org);

        log.info("User {} deleted from organization {}", user.getEmail(), organizationId);
    }
}
