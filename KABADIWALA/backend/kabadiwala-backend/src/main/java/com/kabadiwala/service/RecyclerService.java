package com.kabadiwala.service;

import com.kabadiwala.dto.RecyclerProfileUpdateRequest;
import com.kabadiwala.dto.RecyclerResponse;
import com.kabadiwala.entity.Recycler;
import com.kabadiwala.entity.User;
import com.kabadiwala.exception.ForbiddenException;
import com.kabadiwala.exception.ResourceNotFoundException;
import com.kabadiwala.repository.RecyclerRepository;
import com.kabadiwala.repository.UserRepository;
import com.kabadiwala.security.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RecyclerService {

    private final RecyclerRepository recyclerRepository;
    private final UserRepository userRepository;

    public RecyclerService(RecyclerRepository recyclerRepository, UserRepository userRepository) {
        this.recyclerRepository = recyclerRepository;
        this.userRepository = userRepository;
    }

    public Recycler getAuthenticatedRecycler() {
        String email = SecurityUtils.getCurrentUserEmail();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        return recyclerRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ForbiddenException("Recycler profile does not exist for user: " + email));
    }

    public RecyclerResponse getProfile() {
        Recycler recycler = getAuthenticatedRecycler();
        return mapToRecyclerResponse(recycler);
    }

    @Transactional
    public RecyclerResponse updateProfile(RecyclerProfileUpdateRequest request) {
        Recycler recycler = getAuthenticatedRecycler();

        if (request.getOrganizationName() != null) recycler.setOrganizationName(request.getOrganizationName().trim());
        if (request.getBusinessType() != null) recycler.setBusinessType(request.getBusinessType().trim());
        if (request.getGstNumber() != null) recycler.setGstNumber(request.getGstNumber().trim());
        if (request.getAddress() != null) recycler.setAddress(request.getAddress().trim());
        if (request.getCity() != null) recycler.setCity(request.getCity().trim());
        if (request.getState() != null) recycler.setState(request.getState().trim());
        if (request.getPincode() != null) recycler.setPincode(request.getPincode().trim());
        if (request.getLatitude() != null) recycler.setLatitude(request.getLatitude());
        if (request.getLongitude() != null) recycler.setLongitude(request.getLongitude());
        if (request.getBusinessInfo() != null) recycler.setBusinessInfo(request.getBusinessInfo().trim());

        Recycler updatedRecycler = recyclerRepository.save(recycler);
        return mapToRecyclerResponse(updatedRecycler);
    }

    public RecyclerResponse mapToRecyclerResponse(Recycler recycler) {
        RecyclerResponse response = new RecyclerResponse();
        response.setId(recycler.getId());
        if (recycler.getUser() != null) {
            response.setUserId(recycler.getUser().getId());
            response.setName(recycler.getUser().getName());
            response.setPhone(recycler.getUser().getPhone());
        }
        response.setOrganizationName(recycler.getOrganizationName());
        response.setBusinessType(recycler.getBusinessType());
        response.setGstNumber(recycler.getGstNumber());
        response.setAddress(recycler.getAddress());
        response.setCity(recycler.getCity());
        response.setState(recycler.getState());
        response.setPincode(recycler.getPincode());
        response.setLatitude(recycler.getLatitude());
        response.setLongitude(recycler.getLongitude());
        response.setActive(recycler.isActive());
        response.setVerificationStatus(recycler.getVerificationStatus());
        response.setBusinessInfo(recycler.getBusinessInfo());
        response.setCreatedAt(recycler.getCreatedAt());
        response.setUpdatedAt(recycler.getUpdatedAt());
        return response;
    }
}
