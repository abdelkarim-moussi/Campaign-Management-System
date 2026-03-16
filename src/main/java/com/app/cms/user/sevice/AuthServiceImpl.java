package com.app.cms.user.sevice;

import com.app.cms.common.security.JwtService;
import com.app.cms.common.security.OrganizationContext;
import com.app.cms.common.security.UserDetailsImpl;
import com.app.cms.user.dto.*;
import com.app.cms.user.entity.*;
import com.app.cms.user.mapper.OrganizationMapper;
import com.app.cms.user.mapper.UserMapper;
import com.app.cms.user.repository.InvitationRepository;
import com.app.cms.user.repository.OrganizationRepository;
import com.app.cms.user.repository.UserRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;
    private final InvitationRepository invitationRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserMapper userMapper;
    private final OrganizationMapper organizationMapper;

    @Value("${security.jwt.expiration-time}")
    private long accessTokenExpirationTime;

    @Value("${security.jwt.refresh-token.expiration-time}")
    private long refreshTokenExpirationTime;

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


        return setResponse(user, organizationMapper.toDto(savedOrg), userMapper.toDto(savedUser));
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

        Map<String,String> tokens = jwtService.generateTokenPair(new UserDetailsImpl(user));

        log.info("Login successful: {}", user.getEmail());

        UserDto userDto = userMapper.toDto(user);
        OrganizationDto orgDto = organizationMapper.toDto(user.getOrganization());

        LoginResponse response = new LoginResponse(tokens, userDto, orgDto);
        response.setExpiresIn(accessTokenExpirationTime / 1000);
        response.setSetRefreshExpiresIn(refreshTokenExpirationTime / 1000);

        return response;
    }

    @Transactional
    public LoginResponse acceptInvitation(AcceptInvitationRequest request) {
        log.info("Accepting invitation with token: {}", request.getToken());

        Invitation invitation = invitationRepository.findByToken(request.getToken())
                .orElseThrow(() -> new IllegalArgumentException("Invalid invitation token"));

        if (!invitation.isPending()) {
            throw new IllegalArgumentException("Invitation is no longer valid");
        }

        if (invitation.isExpired()) {
            invitation.setStatus(InvitationStatus.EXPIRED);
            invitationRepository.save(invitation);
            throw new IllegalArgumentException("Invitation has expired");
        }


        if (userRepository.existsByEmail(invitation.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }


        if (!invitation.getOrganization().canAddUser()) {
            throw new IllegalArgumentException("Organization has reached maximum users limit");
        }

        User user = new User();
        user.setOrganization(invitation.getOrganization());
        user.setEmail(invitation.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setRole(invitation.getRole());
        user.setStatus(UserStatus.ACTIVE);
        user.setEmailVerified(true);

        User savedUser = userRepository.save(user);

        invitation.setStatus(InvitationStatus.ACCEPTED);
        invitation.setAcceptedAt(LocalDateTime.now());
        invitationRepository.save(invitation);


        Organization org = invitation.getOrganization();
        org.setCurrentUsers(org.getCurrentUsers() + 1);
        organizationRepository.save(org);

        log.info("Invitation accepted. User created: {}", savedUser.getEmail());

        return setResponse(savedUser, organizationMapper.toDto(org), userMapper.toDto(savedUser));
    }

    @NonNull
    private LoginResponse setResponse(User savedUser, OrganizationDto orgDto, UserDto userDto) {
        Map<String,String> tokens = jwtService.generateTokenPair(new UserDetailsImpl(savedUser));

        LoginResponse response = new LoginResponse(tokens, userDto, orgDto);
        response.setExpiresIn(accessTokenExpirationTime / 1000);
        response.setSetRefreshExpiresIn(refreshTokenExpirationTime / 1000);

        return response;
    }

    @Transactional
    public RefreshTokenResponse refreshToken(RefreshTokenRequest request) {
        log.info("Refreshing tokens");

        try {
            String email = jwtService.extractUserName(request.getRefreshToken());

            String tokenType = jwtService.extractTokenType(request.getRefreshToken());
            if (!"REFRESH".equals(tokenType)) {
                throw new IllegalArgumentException("Invalid token type, expected REFRESH token");
            }

            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

            if (user.getStatus() != UserStatus.ACTIVE) {
                throw new IllegalArgumentException("User account is not active");
            }

            if (!user.getOrganization().isActive()) {
                throw new IllegalArgumentException("Organization is not active");
            }

            UserDetailsImpl userDetails = new UserDetailsImpl(user);

            Map<String, String> tokens = jwtService.refreshTokens(
                    request.getRefreshToken(),
                    userDetails
            );

            log.info("Tokens refreshed successfully for user: {}", email);

            return new RefreshTokenResponse(
                    tokens.get("accessToken"),
                    tokens.get("refreshToken"),
                    accessTokenExpirationTime / 1000,  // Access token expiration
                    refreshTokenExpirationTime / 1000  // Refresh token expiration
            );

        } catch (Exception e) {
            log.error("Failed to refresh tokens: {}", e.getMessage());
            throw new IllegalArgumentException("Invalid or expired refresh token");
        }
    }

    public UserDto getCurrentUser() {
        Long userId = OrganizationContext.getUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return userMapper.toDto(user);
    }
}
