package com.kabadiwala.service;

import com.kabadiwala.dto.AuthResponse;
import com.kabadiwala.dto.LoginRequest;
import com.kabadiwala.dto.RegisterRequest;
import com.kabadiwala.entity.Collector;
import com.kabadiwala.entity.Recycler;
import com.kabadiwala.entity.Role;
import com.kabadiwala.entity.User;
import com.kabadiwala.entity.Wallet;
import com.kabadiwala.exception.ResourceNotFoundException;
import com.kabadiwala.repository.CollectorRepository;
import com.kabadiwala.repository.RecyclerRepository;
import com.kabadiwala.repository.RoleRepository;
import com.kabadiwala.repository.UserRepository;
import com.kabadiwala.repository.WalletRepository;
import com.kabadiwala.security.CustomUserDetails;
import com.kabadiwala.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final CollectorRepository collectorRepository;
    private final RecyclerRepository recyclerRepository;
    private final WalletRepository walletRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       CollectorRepository collectorRepository,
                       RecyclerRepository recyclerRepository,
                       WalletRepository walletRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.collectorRepository = collectorRepository;
        this.recyclerRepository = recyclerRepository;
        this.walletRepository = walletRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email is already registered: " + request.getEmail());
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setActive(true);

        String roleStr = (request.getRole() != null && !request.getRole().isBlank())
                ? request.getRole().toUpperCase() : "USER";
        if (!roleStr.startsWith("ROLE_")) {
            roleStr = "ROLE_" + roleStr;
        }

        String finalRoleName = roleStr;
        Role role = roleRepository.findByName(finalRoleName)
                .orElseGet(() -> roleRepository.save(new Role(finalRoleName, finalRoleName)));
        user.setRoles(Collections.singleton(role));

        User savedUser = userRepository.save(user);

        // Auto-provision profile based on role
        if ("ROLE_COLLECTOR".equals(finalRoleName)) {
            Collector collector = new Collector();
            collector.setUser(savedUser);
            collector.setActive(true);
            collector.setIsAvailable(true);
            collector.setServiceArea("Central");
            collectorRepository.save(collector);
        } else if ("ROLE_RECYCLER".equals(finalRoleName)) {
            Recycler recycler = new Recycler();
            recycler.setUser(savedUser);
            recycler.setFacilityName(savedUser.getName() + " Recycling");
            recycler.setActive(true);
            recyclerRepository.save(recycler);
        }

        // Initialize user wallet with 0.00
        Wallet wallet = new Wallet();
        wallet.setUser(savedUser);
        wallet.setBalance(BigDecimal.ZERO);
        wallet.setCurrency("INR");
        walletRepository.save(wallet);

        CustomUserDetails userDetails = new CustomUserDetails(savedUser);
        String token = jwtService.generateToken(userDetails);
        Set<String> roles = savedUser.getRoles().stream().map(Role::getName).collect(Collectors.toSet());

        return new AuthResponse(token, savedUser.getId(), savedUser.getName(), savedUser.getEmail(), roles);
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + request.getEmail()));

        CustomUserDetails userDetails = new CustomUserDetails(user);
        String token = jwtService.generateToken(userDetails);
        Set<String> roles = user.getRoles().stream().map(Role::getName).collect(Collectors.toSet());

        return new AuthResponse(token, user.getId(), user.getName(), user.getEmail(), roles);
    }
}
