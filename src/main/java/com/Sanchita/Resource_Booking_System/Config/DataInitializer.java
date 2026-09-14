package com.Sanchita.Resource_Booking_System.Config;

import com.Sanchita.Resource_Booking_System.Entity.User;
import com.Sanchita.Resource_Booking_System.Enums.Role;
import com.Sanchita.Resource_Booking_System.Repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {

        // Seed ADMIN
        if (!userRepo.existsByEmailIgnoreCase("admin@gmail.com")) {

            User admin = User.builder()
                    .name("Admin")
                    .email("admin@gmail.com")
                    .password(passwordEncoder.encode("admin@123"))
                    .role(Role.ADMIN)
                    .build();

            userRepo.save(admin);

            System.out.println("Admin user created successfully.");
        }


        if (!userRepo.existsByEmailIgnoreCase("sanchita@gmail.com")) {

            User user = User.builder()
                    .name("Sanchita")
                    .email("sanchita@gmail.com")
                    .password(passwordEncoder.encode("sanchita@123"))
                    .role(Role.USER)
                    .build();

            userRepo.save(user);

            System.out.println("User created successfully.");
        }

    }
}
