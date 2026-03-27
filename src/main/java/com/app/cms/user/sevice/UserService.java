package com.app.cms.user.sevice;

import com.app.cms.user.dto.InviteUserRequest;
import com.app.cms.user.dto.UpdateProfileRequest;
import com.app.cms.user.dto.UserDto;
import com.app.cms.user.entity.Invitation;
import com.app.cms.user.entity.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;



public interface UserService {
    Invitation inviteUser(InviteUserRequest request);

    Page<UserDto> getOrganizationUsers(Pageable pageable);
    UserDto updateProfile(UpdateProfileRequest request);
    UserDto updateUserRole(Long userId, UserRole newRole);
    void deleteUser(Long userId);
}
