package com.app.cms.user.web;

import com.app.cms.user.dto.InviteUserRequest;
import com.app.cms.user.dto.UpdateProfileRequest;
import com.app.cms.user.dto.UserDto;
import com.app.cms.user.entity.Invitation;
import com.app.cms.user.entity.UserRole;
import com.app.cms.user.sevice.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;


    @PutMapping("/me")
    public ResponseEntity<UserDto> updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
        UserDto user = userService.updateProfile(request);
        return ResponseEntity.ok(user);
    }


    @GetMapping
    public ResponseEntity<List<UserDto>> getOrganizationUsers() {
        List<UserDto> users = userService.getOrganizationUsers();
        return ResponseEntity.ok(users);
    }

    @PostMapping("/invite")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<Invitation> inviteUser(@Valid @RequestBody InviteUserRequest request) {
        Invitation invitation = userService.inviteUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(invitation);
    }


    @PatchMapping("/{userId}/role")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<UserDto> updateUserRole(
            @PathVariable Long userId,
            @RequestParam UserRole role) {
        UserDto user = userService.updateUserRole(userId, role);
        return ResponseEntity.ok(user);
    }


    @DeleteMapping("/{userId}")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
}
