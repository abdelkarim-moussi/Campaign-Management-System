package com.app.cms.user.service;

import com.app.cms.common.security.OrganizationContext;
import com.app.cms.user.dto.InviteUserRequest;
import com.app.cms.user.dto.UpdateProfileRequest;
import com.app.cms.user.dto.UserDto;
import com.app.cms.user.entity.*;
import com.app.cms.user.mapper.UserMapper;
import com.app.cms.user.repository.InvitationRepository;
import com.app.cms.user.repository.OrganizationRepository;
import com.app.cms.user.repository.UserRepository;
import com.app.cms.user.sevice.UserServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private OrganizationRepository organizationRepository;
    @Mock private InvitationRepository invitationRepository;
    @Mock private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private MockedStatic<OrganizationContext> mockedContext;
    private final Long ORG_ID = 1L;
    private final Long USER_ID = 10L;

    @BeforeEach
    void setUp() {
        mockedContext = mockStatic(OrganizationContext.class);
        mockedContext.when(OrganizationContext::getOrganizationId).thenReturn(ORG_ID);
        mockedContext.when(OrganizationContext::getUserId).thenReturn(USER_ID);
    }

    @AfterEach
    void tearDown() {
        mockedContext.close();
    }

    // --- inviteUser Tests ---

    @Test
    void inviteUser_shouldSucceed_whenPermissionsAndLimitsAreValid() {
        // Arrange
        InviteUserRequest request = new InviteUserRequest("new@test.com", UserRole.MEMBER);

        Organization org = new Organization();
        org.setMaxUsers(5);
        org.setCurrentUsers(2);

        User currentUser = mock(User.class);
        when(currentUser.canManageUsers()).thenReturn(true);
        when(currentUser.getOrganization()).thenReturn(org);

        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(currentUser));
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(invitationRepository.existsByEmailAndOrganizationIdAndStatus(any(), any(), any())).thenReturn(false);
        when(invitationRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        // Act
        Invitation result = userService.inviteUser(request);

        // Assert
        assertNotNull(result);
        assertEquals(request.getEmail(), result.getEmail());
        verify(invitationRepository).save(any(Invitation.class));
    }

    @Test
    void inviteUser_shouldThrowSecurityException_whenUserCannotManageUsers() {
        InviteUserRequest request = new InviteUserRequest("new@test.com", UserRole.MEMBER);
        User currentUser = mock(User.class);
        when(currentUser.canManageUsers()).thenReturn(false);
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(currentUser));

        assertThrows(SecurityException.class, () -> userService.inviteUser(request));
    }

    @Test
    void inviteUser_shouldThrowException_whenOrgLimitReached() {
        InviteUserRequest request = new InviteUserRequest("new@test.com", UserRole.MEMBER);
        Organization org = mock(Organization.class);
        when(org.canAddUser()).thenReturn(false);

        User currentUser = mock(User.class);
        when(currentUser.canManageUsers()).thenReturn(true);
        when(currentUser.getOrganization()).thenReturn(org);

        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(currentUser));

        assertThrows(IllegalArgumentException.class, () -> userService.inviteUser(request));
    }

    // --- updateProfile Tests ---

    @Test
    void updateProfile_shouldUpdateOnlyNonNullFields() {
        // Arrange
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setFirstName("NewName");

        User user = new User();
        user.setFirstName("OldName");
        user.setLastName("StaySame");

        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(userMapper.toDto(any())).thenReturn(new UserDto());

        // Act
        userService.updateProfile(request);

        // Assert
        assertEquals("NewName", user.getFirstName());
        assertEquals("StaySame", user.getLastName());
        verify(userRepository).save(user);
    }

    // --- updateUserRole Tests ---

    @Test
    void updateUserRole_shouldAllowOwnerToMakeAdmin() {
        // Arrange
        Long targetUserId = 20L;
        User currentUser = mock(User.class);
        User targetUser = mock(User.class);

        when(currentUser.canManageUsers()).thenReturn(true);
        when(currentUser.isOwner()).thenReturn(true);
        when(targetUser.isOwner()).thenReturn(false);

        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(currentUser));
        when(userRepository.findByIdAndOrganizationId(targetUserId, ORG_ID)).thenReturn(Optional.of(targetUser));
        when(userRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        // Act
        userService.updateUserRole(targetUserId, UserRole.ADMIN);

        // Assert
        verify(targetUser).setRole(UserRole.ADMIN);
        verify(userRepository).save(targetUser);
    }

    @Test
    void updateUserRole_shouldBlockAdminFromMakingOtherAdmin() {
        // Arrange
        Long targetUserId = 20L;
        User currentUser = mock(User.class);
        User targetUser = mock(User.class);

        when(currentUser.canManageUsers()).thenReturn(true);
        when(currentUser.isOwner()).thenReturn(false); // Only Admin
        when(targetUser.isOwner()).thenReturn(false);

        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(currentUser));
        when(userRepository.findByIdAndOrganizationId(targetUserId, ORG_ID)).thenReturn(Optional.of(targetUser));

        // Act & Assert
        assertThrows(SecurityException.class, () -> userService.updateUserRole(targetUserId, UserRole.ADMIN));
    }

    // --- deleteUser Tests ---

    @Test
    void deleteUser_shouldDecrementOrgCounterOnSuccess() {
        // Arrange
        Long targetUserId = 20L;
        Organization org = new Organization();
        org.setCurrentUsers(5);

        User currentUser = mock(User.class);
        User targetUser = mock(User.class);

        when(currentUser.canManageUsers()).thenReturn(true);
        when(targetUser.getId()).thenReturn(targetUserId);
        when(targetUser.getOrganization()).thenReturn(org);
        when(targetUser.isOwner()).thenReturn(false);

        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(currentUser));
        when(userRepository.findByIdAndOrganizationId(targetUserId, ORG_ID)).thenReturn(Optional.of(targetUser));

        // Act
        userService.deleteUser(targetUserId);

        // Assert
        assertEquals(4, org.getCurrentUsers());
        verify(userRepository).delete(targetUser);
        verify(organizationRepository).save(org);
    }

    @Test
    void deleteUser_shouldPreventDeletingSelf() {
        // Arrange
        User currentUser = mock(User.class);
        User targetUser = mock(User.class);

        when(currentUser.canManageUsers()).thenReturn(true);
        when(targetUser.getId()).thenReturn(USER_ID); // Same as current

        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(currentUser));
        when(userRepository.findByIdAndOrganizationId(USER_ID, ORG_ID)).thenReturn(Optional.of(targetUser));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> userService.deleteUser(USER_ID));
    }
}