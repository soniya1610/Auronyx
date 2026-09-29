package com.kabadiwala.service;

import com.kabadiwala.dto.*;
import com.kabadiwala.entity.User;
import com.kabadiwala.exception.ResourceNotFoundException;
import com.kabadiwala.repository.NotificationRepository;
import com.kabadiwala.repository.UserRepository;
import com.kabadiwala.security.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;
    private final AuthService authService;

    public UserService(
            UserRepository userRepository,
            NotificationRepository notificationRepository,
            AuthService authService
    ) {
        this.userRepository = userRepository;
        this.notificationRepository = notificationRepository;
        this.authService = authService;
    }

    public User getAuthenticatedUser() {
        String email = SecurityUtils.getCurrentUserEmail();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found with email: " + email));
    }

    public UserResponse getProfile() {
        User user = getAuthenticatedUser();
        return authService.mapToUserResponse(user);
    }

    @Transactional
    public UserResponse updateProfile(UserUpdateRequest request) {
        User user = getAuthenticatedUser();

        if (request.getName() != null && !request.getName().trim().isEmpty()) {
            user.setName(request.getName().trim());
        }
        if (request.getAddress() != null) {
            user.setAddress(request.getAddress().trim());
        }
        if (request.getCity() != null) {
            user.setCity(request.getCity().trim());
        }
        if (request.getState() != null) {
            user.setState(request.getState().trim());
        }
        if (request.getPincode() != null) {
            user.setPincode(request.getPincode().trim());
        }

        User updatedUser = userRepository.save(user);
        return authService.mapToUserResponse(updatedUser);
    }

    @Transactional
    public UserResponse updateLocation(LocationUpdateRequest request) {
        User user = getAuthenticatedUser();

        if (request.getAddress() != null) {
            user.setAddress(request.getAddress().trim());
        }
        if (request.getCity() != null) {
            user.setCity(request.getCity().trim());
        }
        if (request.getState() != null) {
            user.setState(request.getState().trim());
        }
        if (request.getPincode() != null) {
            user.setPincode(request.getPincode().trim());
        }
        user.setLatitude(request.getLatitude());
        user.setLongitude(request.getLongitude());

        User updatedUser = userRepository.save(user);
        return authService.mapToUserResponse(updatedUser);
    }

    @Transactional
    public UserResponse updateLanguage(LanguageUpdateRequest request) {
        User user = getAuthenticatedUser();
        user.setPreferredLanguage(request.getLanguage().trim().toLowerCase());

        User updatedUser = userRepository.save(user);
        return authService.mapToUserResponse(updatedUser);
    }

    public UserDashboardResponse getDashboard() {
        User user = getAuthenticatedUser();
        long unreadCount = notificationRepository.countByUserIdAndIsReadFalse(user.getId());
        UserResponse userResponse = authService.mapToUserResponse(user);

        return new UserDashboardResponse(userResponse, unreadCount, user.getPreferredLanguage());
    }

    public UserImpactResponse getImpact() {
        // Enforce user authentication
        getAuthenticatedUser();
        return new UserImpactResponse();
    }
}
