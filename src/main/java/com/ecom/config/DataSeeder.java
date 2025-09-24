package com.ecom.config;

import com.ecom.entity.Role;
import com.ecom.entity.User;
import com.ecom.repo.UserRepository;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataSeeder {

  private final UserRepository userRepo;
  private final PasswordEncoder encoder;

  public DataSeeder(UserRepository userRepo, PasswordEncoder encoder) {
    this.userRepo = userRepo;
    this.encoder = encoder;
  }

  @Bean
  ApplicationRunner initAdmin() {
    return args -> {
      String adminEmail = "admin@example.com";
      if (!userRepo.existsByEmail(adminEmail)) {
        User admin = new User();
        admin.setName("Admin");
        admin.setEmail(adminEmail);
        admin.setPassword(encoder.encode("admin123"));
        admin.setRole(Role.ROLE_ADMIN);
        admin.setEnabled(true);
        userRepo.save(admin);
        System.out.println("===> Seeded admin: " + adminEmail + " / admin123");
      }
    };
  }
}

