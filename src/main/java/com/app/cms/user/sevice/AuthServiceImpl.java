package com.app.cms.user.sevice;

import com.app.cms.common.security.JwtService;
import com.app.cms.user.dto.*;
import com.app.cms.user.entity.*;
import com.app.cms.user.mapper.OrganizationMapper;
import com.app.cms.user.mapper.UserMapper;
import com.app.cms.user.repository.InvitationRepository;
import com.app.cms.user.repository.OrganizationRepository;
import com.app.cms.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl {
    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;
    private final InvitationRepository invitationRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserMapper userMapper;
    private final OrganizationMapper organizationMapper;

    @Transactional
    public LoginResponse register(RegisterRequest request) {
        log.info("Registering new user: {}", request.getEmail());

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + request.getEmail());
        }

        Organization organization = new Organization();
        organization.setName(request.getOrganizationName());
        organization.setEmail(request.getEmail());
        organization.setPlan(OrganizationPlan.FREE);
        organization.setStatus(OrganizationStatus.TRIAL);
        organization.setCurrentUsers(1);

        Organization savedOrg = organizationRepository.save(organization);
        log.info("Organization created: {} ({})", savedOrg.getName(), savedOrg.getId());

        User user = new User();
        user.setOrganization(savedOrg);
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setRole(UserRole.OWNER);
        user.setStatus(UserStatus.ACTIVE);
        user.setEmailVerified(true);

        User savedUser = userRepository.save(user);
        log.info("User created: {} with role OWNER", savedUser.getEmail());

        String token = jwtService.generateTokenPair(user);

        UserDto userDto = userMapper.toDto(savedUser);
        OrganizationDto orgDto = organizationMapper.toDto(savedOrg);

        return new LoginResponse(token, userDto, orgDto);
    }

    @Transactional
    public LoginResponse login(LoginRequest request) {
        log.info("Login attempt: {}", request.getEmail());

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password");
        }


        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new IllegalArgumentException("Account is not active");
        }


        if (!user.getOrganization().isActive()) {
            throw new IllegalArgumentException("Organization is suspended or cancelled");
        }


        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);

        String token = jwtService.generateTokenPair(user);

        log.info("Login successful: {}", user.getEmail());

        UserDto userDto = userMapper.toDto(user);
        OrganizationDto orgDto = organizationMapper.toDto(user.getOrganization());

        return new LoginResponse(token, userDto, orgDto);
    }
}
