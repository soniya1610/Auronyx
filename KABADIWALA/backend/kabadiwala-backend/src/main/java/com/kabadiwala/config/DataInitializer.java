package com.kabadiwala.config;

import com.kabadiwala.entity.*;
import com.kabadiwala.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final CollectorRepository collectorRepository;
    private final RecyclerRepository recyclerRepository;
    private final WasteCategoryRepository wasteCategoryRepository;
    private final RewardRepository rewardRepository;
    private final PickupRepository pickupRepository;
    private final WalletTransactionRepository walletTransactionRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(
            RoleRepository roleRepository,
            UserRepository userRepository,
            CollectorRepository collectorRepository,
            RecyclerRepository recyclerRepository,
            WasteCategoryRepository wasteCategoryRepository,
            RewardRepository rewardRepository,
            PickupRepository pickupRepository,
            WalletTransactionRepository walletTransactionRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.collectorRepository = collectorRepository;
        this.recyclerRepository = recyclerRepository;
        this.wasteCategoryRepository = wasteCategoryRepository;
        this.rewardRepository = rewardRepository;
        this.pickupRepository = pickupRepository;
        this.walletTransactionRepository = walletTransactionRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // 1. Seed Roles
        Role userRole = roleRepository.findByName(RoleType.ROLE_USER)
                .orElseGet(() -> roleRepository.save(new Role(RoleType.ROLE_USER)));
        Role collectorRole = roleRepository.findByName(RoleType.ROLE_COLLECTOR)
                .orElseGet(() -> roleRepository.save(new Role(RoleType.ROLE_COLLECTOR)));
        Role recyclerRole = roleRepository.findByName(RoleType.ROLE_RECYCLER)
                .orElseGet(() -> roleRepository.save(new Role(RoleType.ROLE_RECYCLER)));
        Role adminRole = roleRepository.findByName(RoleType.ROLE_ADMIN)
                .orElseGet(() -> roleRepository.save(new Role(RoleType.ROLE_ADMIN)));

        // 2. Seed Default Admin
        if (userRepository.findByEmail("admin@kabadiwala.com").isEmpty()) {
            User admin = new User();
            admin.setName("Auronyx Admin");
            admin.setEmail("admin@kabadiwala.com");
            admin.setPhone("9999900000");
            admin.setPassword(passwordEncoder.encode("Admin@123"));
            admin.setCity("Mumbai");
            admin.setState("Maharashtra");
            admin.setPincode("400001");
            Set<Role> roles = new HashSet<>();
            roles.add(adminRole);
            roles.add(userRole);
            admin.setRoles(roles);
            userRepository.save(admin);
        }

        // 3. Seed Demo Collector
        User collectorUser = userRepository.findByEmail("collector@kabadiwala.com").orElse(null);
        if (collectorUser == null) {
            collectorUser = new User();
            collectorUser.setName("Ramesh Kumar (Kabadi Collector)");
            collectorUser.setEmail("collector@kabadiwala.com");
            collectorUser.setPhone("9888800001");
            collectorUser.setPassword(passwordEncoder.encode("Collector@123"));
            collectorUser.setAddress("Shop #12, Bandra West Scrap Yard");
            collectorUser.setCity("Mumbai");
            collectorUser.setState("Maharashtra");
            collectorUser.setPincode("400050");
            collectorUser.setLatitude(19.0596);
            collectorUser.setLongitude(72.8295);
            Set<Role> roles = new HashSet<>();
            roles.add(collectorRole);
            roles.add(userRole);
            collectorUser.setRoles(roles);
            collectorUser = userRepository.save(collectorUser);

            Collector collector = new Collector();
            collector.setUser(collectorUser);
            collector.setVehicleType("Three-Wheeler Electric Auto");
            collector.setVehicleNumber("MH-02-EQ-4421");
            collector.setServiceArea("Bandra, Khar, Santacruz");
            collector.setAddress("Shop #12, Bandra West Scrap Yard");
            collector.setCity("Mumbai");
            collector.setState("Maharashtra");
            collector.setPincode("400050");
            collector.setLatitude(19.0596);
            collector.setLongitude(72.8295);
            collector.setAvailable(true);
            collector.setActive(true);
            collector.setVerificationStatus("VERIFIED");
            collector.setKycStatus("VERIFIED");
            collector.setWorkingHours("8:00 AM - 7:00 PM");
            collectorRepository.save(collector);
        }

        // 4. Seed Demo Recycler
        User recyclerUser = userRepository.findByEmail("recycler@kabadiwala.com").orElse(null);
        if (recyclerUser == null) {
            recyclerUser = new User();
            recyclerUser.setName("GreenPulse Recyclers Pvt Ltd");
            recyclerUser.setEmail("recycler@kabadiwala.com");
            recyclerUser.setPhone("9777700002");
            recyclerUser.setPassword(passwordEncoder.encode("Recycler@123"));
            recyclerUser.setAddress("Plot 44, MIDC Industrial Area");
            recyclerUser.setCity("Mumbai");
            recyclerUser.setState("Maharashtra");
            recyclerUser.setPincode("400093");
            recyclerUser.setLatitude(19.1176);
            recyclerUser.setLongitude(72.8631);
            Set<Role> roles = new HashSet<>();
            roles.add(recyclerRole);
            roles.add(userRole);
            recyclerUser.setRoles(roles);
            recyclerUser = userRepository.save(recyclerUser);

            Recycler recycler = new Recycler();
            recycler.setUser(recyclerUser);
            recycler.setOrganizationName("GreenPulse Circular Solutions");
            recycler.setBusinessType("Authorized Plastic & E-Waste Recycler");
            recycler.setGstNumber("27AAACG1234F1Z5");
            recycler.setAddress("Plot 44, MIDC Industrial Area");
            recycler.setCity("Mumbai");
            recycler.setState("Maharashtra");
            recycler.setPincode("400093");
            recycler.setLatitude(19.1176);
            recycler.setLongitude(72.8631);
            recycler.setActive(true);
            recycler.setVerificationStatus("VERIFIED");
            recycler.setBusinessInfo("CPCB and MPCB certified plastic and electronic waste recycling unit with 50 tons/month capacity.");
            recyclerRepository.save(recycler);
        }

        // 5. Seed Demo Citizen User
        User citizenUser = userRepository.findByEmail("user@kabadiwala.com").orElse(null);
        if (citizenUser == null) {
            citizenUser = new User();
            citizenUser.setName("Priya Sharma");
            citizenUser.setEmail("user@kabadiwala.com");
            citizenUser.setPhone("9666600003");
            citizenUser.setPassword(passwordEncoder.encode("User@123"));
            citizenUser.setAddress("A-402, Green Meadows, Linking Road");
            citizenUser.setCity("Mumbai");
            citizenUser.setState("Maharashtra");
            citizenUser.setPincode("400050");
            citizenUser.setLatitude(19.0620);
            citizenUser.setLongitude(72.8310);
            Set<Role> roles = new HashSet<>();
            roles.add(userRole);
            citizenUser.setRoles(roles);
            citizenUser = userRepository.save(citizenUser);

            // Add starter wallet credit
            walletTransactionRepository.save(new WalletTransaction(citizenUser.getId(), 120.0, "CREDIT", "Welcome Bonus & First Scrap Payout", null));
        }

        // 6. Seed Waste Categories
        if (wasteCategoryRepository.count() == 0) {
            wasteCategoryRepository.save(new WasteCategory("Plastic (PET/HDPE)", "PLASTIC", "Water bottles, milk pouches, plastic jars, clean containers", 16.0, "kg", "Recycle", 1.8));
            wasteCategoryRepository.save(new WasteCategory("Cardboard & Paper", "PAPER", "Newspapers, books, brown cartons, cardboard packaging", 14.0, "kg", "FileText", 1.2));
            wasteCategoryRepository.save(new WasteCategory("Iron & Steel Scrap", "METAL", "Old utensils, iron rods, bike parts, steel scrap", 28.0, "kg", "Hammer", 2.0));
            wasteCategoryRepository.save(new WasteCategory("Electronic & E-Waste", "E_WASTE", "Old phones, chargers, circuit boards, small appliances", 45.0, "kg", "Cpu", 3.5));
            wasteCategoryRepository.save(new WasteCategory("Glass Bottles", "GLASS", "Intact glass beer/sauce/pickle bottles", 4.0, "kg", "Wine", 0.5));
            wasteCategoryRepository.save(new WasteCategory("Copper & Brass Wire", "COPPER", "Stripped copper wire, brass fittings and taps", 420.0, "kg", "Zap", 4.5));
        }

        // 7. Seed Reward Catalog
        if (rewardRepository.count() == 0) {
            rewardRepository.save(new Reward("₹100 Grocery Voucher", "Instant ₹100 discount on BigBasket, Blinkit, or Zepto", 200, "₹100", "VOUCHER", "Blinkit", "BLINK100", "ShoppingBag"));
            rewardRepository.save(new Reward("Plant a Native Tree", "We will plant a neem or banyan tree with geotagged certificate", 350, "1 Tree", "TREE", "GreenEarth NGO", "TREEPLANT", "Trees"));
            rewardRepository.save(new Reward("₹250 Clean Energy Credit", "Direct cashback into your green power utility account", 500, "₹250", "CASHBACK", "Auronyx Eco", "CLEAN250", "Sun"));
            rewardRepository.save(new Reward("Upcycled Eco Tote Bag", "Durable tote bag crafted from recycled post-consumer canvas", 150, "Free Gift", "ECO_MERCH", "Auronyx Upcycle", "TOTEBAG", "Package"));
        }

        // 8. Seed Sample Pickups for Citizen
        if (pickupRepository.count() == 0 && citizenUser != null) {
            Collector col = collectorRepository.findByUserId(collectorUser.getId()).orElse(null);

            Pickup p1 = new Pickup();
            p1.setUser(citizenUser);
            p1.setCollector(col);
            p1.setCategoryName("Cardboard & Paper");
            p1.setStatus("ASSIGNED");
            p1.setScheduledDate("Today");
            p1.setTimeSlot("2:00 PM - 4:00 PM");
            p1.setAddress("A-402, Green Meadows, Linking Road");
            p1.setCity("Mumbai");
            p1.setPincode("400050");
            p1.setNotes("Old cartons flattened near front door");
            p1.setEstimatedWeightKg(6.5);
            p1.setEstimatedAmount(91.0);
            p1.setVerificationCode("4819");
            pickupRepository.save(p1);

            Pickup p2 = new Pickup();
            p2.setUser(citizenUser);
            p2.setCollector(col);
            p2.setCategoryName("Plastic (PET/HDPE)");
            p2.setStatus("COMPLETED");
            p2.setScheduledDate("Yesterday");
            p2.setTimeSlot("10:00 AM - 12:00 PM");
            p2.setAddress("A-402, Green Meadows, Linking Road");
            p2.setCity("Mumbai");
            p2.setPincode("400050");
            p2.setEstimatedWeightKg(4.0);
            p2.setEstimatedAmount(64.0);
            p2.setActualWeightKg(4.5);
            p2.setFinalAmount(72.0);
            p2.setVerificationCode("8392");
            p2.setPaymentStatus("PAID");
            pickupRepository.save(p2);
        }
    }
}
