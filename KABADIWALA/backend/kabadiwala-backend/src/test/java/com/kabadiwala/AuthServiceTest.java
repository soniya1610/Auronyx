package com.kabadiwala;

import com.kabadiwala.repository.*;
import com.kabadiwala.security.JwtService;
import com.kabadiwala.service.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class AuthServiceTest {

    @Test
    @DisplayName("AuthService can be instantiated and initialized")
    void shouldInitializeAuthService() {
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        RoleRepository roleRepository = Mockito.mock(RoleRepository.class);
        CollectorRepository collectorRepository = Mockito.mock(CollectorRepository.class);
        RecyclerRepository recyclerRepository = Mockito.mock(RecyclerRepository.class);
        WalletRepository walletRepository = Mockito.mock(WalletRepository.class);
        PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);
        JwtService jwtService = Mockito.mock(JwtService.class);
        AuthenticationManager authenticationManager = Mockito.mock(AuthenticationManager.class);

        AuthService authService = new AuthService(
                userRepository,
                roleRepository,
                collectorRepository,
                recyclerRepository,
                walletRepository,
                passwordEncoder,
                jwtService,
                authenticationManager
        );

        assertNotNull(authService);
    }
}
