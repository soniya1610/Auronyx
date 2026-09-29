package com.kabadiwala.config;

import com.kabadiwala.entity.*;
import com.kabadiwala.repository.*;
import com.kabadiwala.service.WalletService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final CollectorRepository collectorRepository;
    private final RecyclerRepository recyclerRepository;
    private final WasteCategoryRepository wasteCategoryRepository;
    private final RewardRepository rewardRepository;
    private final WalletService walletService;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(
            RoleRepository roleRepository,
            UserRepository userRepository,
            CollectorRepository collectorRepository,
            RecyclerRepository recyclerRepository,
            WasteCategoryRepository wasteCategoryRepository,
            RewardRepository rewardRepository,
            WalletService walletService,
            PasswordEncoder passwordEncoder
    ) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.collectorRepository = collectorRepository;
        this.recyclerRepository = recyclerRepository;
        this.wasteCategoryRepository = wasteCategoryRepository;
        this.rewardRepository = rewardRepository;
        this.walletService = walletService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        try {
            // 1. Ensure Roles
            Role userRole = roleRepository.findByName("ROLE_USER")
                    .orElseGet(() -> roleRepository.save(new Role("ROLE_USER", "Standard Citizen User")));
            Role collectorRole = roleRepository.findByName("ROLE_COLLECTOR")
                    .orElseGet(() -> roleRepository.save(new Role("ROLE_COLLECTOR", "Waste Collector Partner")));
            Role recyclerRole = roleRepository.findByName("ROLE_RECYCLER")
                    .orElseGet(() -> roleRepository.save(new Role("ROLE_RECYCLER", "Recycling Facility Partner")));
            Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                    .orElseGet(() -> roleRepository.save(new Role("ROLE_ADMIN", "System Administrator")));

            // 2. Demo Admin
            if (userRepository.findByEmail("admin@kabadiwala.com").isEmpty()) {
                User admin = new User("Auronyx Admin", "admin@kabadiwala.com", "9999900000", passwordEncoder.encode("Admin@123"));
                Set<Role> roles = new HashSet<>();
                roles.add(adminRole);
                roles.add(userRole);
                admin.setRoles(roles);
                userRepository.save(admin);
                logger.info("Initialized default Admin: admin@kabadiwala.com");
            }

            // 3. Demo Collector
            User collectorUser = userRepository.findByEmail("collector@kabadiwala.com").orElse(null);
            if (collectorUser == null) {
                collectorUser = new User("Ramesh Kumar (Collector)", "collector@kabadiwala.com", "9888800001", passwordEncoder.encode("Collector@123"));
                Set<Role> roles = new HashSet<>();
                roles.add(collectorRole);
                roles.add(userRole);
                collectorUser.setRoles(roles);
                collectorUser = userRepository.save(collectorUser);

                Collector collector = new Collector(collectorUser, "MH-02-EQ-4421", "Bandra, Khar, Santacruz");
                collector.setLatitude(19.0596);
                collector.setLongitude(72.8295);
                collector.setActive(true);
                collector.setIsAvailable(true);
                collectorRepository.save(collector);
                logger.info("Initialized demo Collector: collector@kabadiwala.com");
            }

            // 4. Demo Recycler
            User recyclerUser = userRepository.findByEmail("recycler@kabadiwala.com").orElse(null);
            if (recyclerUser == null) {
                recyclerUser = new User("GreenPulse Recyclers", "recycler@kabadiwala.com", "9777700002", passwordEncoder.encode("Recycler@123"));
                Set<Role> roles = new HashSet<>();
                roles.add(recyclerRole);
                roles.add(userRole);
                recyclerUser.setRoles(roles);
                recyclerUser = userRepository.save(recyclerUser);

                Recycler recycler = new Recycler(recyclerUser, "GreenPulse Circular Solutions", "Authorized Plastic & E-Waste Recycler");
                recycler.setActive(true);
                recyclerRepository.save(recycler);
                logger.info("Initialized demo Recycler: recycler@kabadiwala.com");
            }

            // 5. Demo Citizen User
            User citizenUser = userRepository.findByEmail("user@kabadiwala.com").orElse(null);
            if (citizenUser == null) {
                citizenUser = new User("Priya Sharma", "user@kabadiwala.com", "9666600003", passwordEncoder.encode("User@123"));
                Set<Role> roles = new HashSet<>();
                roles.add(userRole);
                citizenUser.setRoles(roles);
                citizenUser = userRepository.save(citizenUser);

                // Initialize starter wallet with balance
                Wallet wallet = walletService.getOrCreateWallet(citizenUser);
                walletService.credit(wallet, new BigDecimal("120.00"), "INIT", "INIT-1");
                logger.info("Initialized demo Citizen User: user@kabadiwala.com with starter wallet");
            }

            // 6. Waste Categories
            if (wasteCategoryRepository.count() == 0) {
                wasteCategoryRepository.save(new WasteCategory("Plastic (PET/HDPE)", "Water bottles, milk pouches, plastic jars, clean containers", true, "KG"));
                wasteCategoryRepository.save(new WasteCategory("Cardboard & Paper", "Newspapers, books, brown cartons, cardboard packaging", true, "KG"));
                wasteCategoryRepository.save(new WasteCategory("Iron & Steel Scrap", "Old utensils, iron rods, bike parts, steel scrap", true, "KG"));
                wasteCategoryRepository.save(new WasteCategory("Electronic & E-Waste", "Old phones, chargers, circuit boards, small appliances", true, "KG"));
                wasteCategoryRepository.save(new WasteCategory("Glass Bottles", "Intact glass beer/sauce/pickle bottles", true, "KG"));
                wasteCategoryRepository.save(new WasteCategory("Copper & Brass Wire", "Stripped copper wire, brass fittings and taps", true, "KG"));
                logger.info("Initialized default Waste Categories");
            }

            // 7. Rewards Catalog
            if (rewardRepository.count() == 0) {
                Reward r1 = new Reward();
                r1.setTitle("₹100 Grocery Voucher");
                r1.setDescription("Instant ₹100 discount on BigBasket, Blinkit, or Zepto");
                r1.setType(Reward.RewardType.VOUCHER);
                r1.setPointsCost(200);
                r1.setCashValue(new BigDecimal("100.00"));
                r1.setActive(true);
                rewardRepository.save(r1);

                Reward r2 = new Reward();
                r2.setTitle("Plant a Native Tree");
                r2.setDescription("We will plant a neem or banyan tree with geotagged certificate");
                r2.setType(Reward.RewardType.DONATION);
                r2.setPointsCost(350);
                r2.setCashValue(new BigDecimal("150.00"));
                r2.setActive(true);
                rewardRepository.save(r2);

                Reward r3 = new Reward();
                r3.setTitle("₹250 Clean Energy Cashback");
                r3.setDescription("Direct cashback into your wallet balance");
                r3.setType(Reward.RewardType.CASH_BACK);
                r3.setPointsCost(500);
                r3.setCashValue(new BigDecimal("250.00"));
                r3.setActive(true);
                rewardRepository.save(r3);
                logger.info("Initialized default Rewards Catalog");
            }

        } catch (Exception e) {
            logger.warn("DataInitializer skipped seeding (tables already populated or initialized): {}", e.getMessage());
        }
    }
}
