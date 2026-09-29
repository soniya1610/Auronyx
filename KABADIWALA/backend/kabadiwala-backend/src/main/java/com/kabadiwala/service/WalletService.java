package com.kabadiwala.service;

import com.kabadiwala.entity.User;
import com.kabadiwala.entity.WalletTransaction;
import com.kabadiwala.exception.ResourceNotFoundException;
import com.kabadiwala.repository.UserRepository;
import com.kabadiwala.repository.WalletTransactionRepository;
import com.kabadiwala.security.SecurityUtils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class WalletService {

    private final WalletTransactionRepository walletTransactionRepository;
    private final UserRepository userRepository;

    public WalletService(WalletTransactionRepository walletTransactionRepository, UserRepository userRepository) {
        this.walletTransactionRepository = walletTransactionRepository;
        this.userRepository = userRepository;
    }

    private User getAuthenticatedUser() {
        String email = SecurityUtils.getCurrentUserEmail();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found"));
    }

    public Map<String, Object> getBalance() {
        User user = getAuthenticatedUser();
        List<WalletTransaction> transactions = walletTransactionRepository.findByUserIdOrderByCreatedAtDesc(user.getId());

        double totalCredits = transactions.stream()
                .filter(t -> "CREDIT".equalsIgnoreCase(t.getType()))
                .mapToDouble(WalletTransaction::getAmount)
                .sum();

        double totalDebits = transactions.stream()
                .filter(t -> "DEBIT".equalsIgnoreCase(t.getType()))
                .mapToDouble(WalletTransaction::getAmount)
                .sum();

        double currentBalance = Math.round((totalCredits - totalDebits) * 100.0) / 100.0;
        int kabadiPoints = (int) Math.round(totalCredits * 5) + 150; // base starter points + earned

        Map<String, Object> response = new HashMap<>();
        response.put("balance", currentBalance);
        response.put("currency", "INR");
        response.put("currencySymbol", "₹");
        response.put("kabadiPoints", kabadiPoints);
        response.put("totalEarned", totalCredits);
        return response;
    }

    public List<WalletTransaction> getTransactions() {
        User user = getAuthenticatedUser();
        return walletTransactionRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
    }
}
