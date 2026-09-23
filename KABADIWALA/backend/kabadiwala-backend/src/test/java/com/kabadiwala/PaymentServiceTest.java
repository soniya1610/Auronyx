package com.kabadiwala;

import com.kabadiwala.dto.WalletDto;
import com.kabadiwala.entity.User;
import com.kabadiwala.entity.Wallet;
import com.kabadiwala.service.PaymentService;
import com.kabadiwala.service.TransactionService;
import com.kabadiwala.service.WalletService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class PaymentServiceTest {

    @Test
    @DisplayName("Should map Wallet entity correctly to WalletDto")
    void shouldMapWalletCorrectly() {
        WalletService walletService = Mockito.mock(WalletService.class);
        TransactionService transactionService = Mockito.mock(TransactionService.class);
        PaymentService paymentService = new PaymentService(walletService, transactionService);

        User user = new User();
        user.setId(7L);

        Wallet wallet = new Wallet(user);
        wallet.setId(21L);
        wallet.setBalance(new BigDecimal("350.00"));

        WalletDto dto = paymentService.mapWallet(wallet);

        assertNotNull(dto);
        assertEquals(21L, dto.getId());
        assertEquals(7L, dto.getUserId());
        assertEquals(new BigDecimal("350.00"), dto.getBalance());
    }
}
