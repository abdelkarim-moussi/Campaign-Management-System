package com.app.cms.user.sevice;

import com.app.cms.user.dto.InviteUserRequest;
import com.app.cms.user.dto.UpdateProfileRequest;
import com.app.cms.user.dto.UserDto;
import com.app.cms.user.entity.Invitation;
import com.app.cms.user.entity.UserRole;

import java.util.List;

public interface UserService {
    Invitation inviteUser(InviteUserRequest request);
    List<UserDto> getOrganizationUsers();
    UserDto updateProfile(UpdateProfileRequest request);
    UserDto updateUserRole(Long userId, UserRole newRole);
    void deleteUser(Long userId);
}
