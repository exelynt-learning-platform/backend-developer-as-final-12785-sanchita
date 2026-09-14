package com.Sanchita.Resource_Booking_System.Services.Impl;

import com.Sanchita.Resource_Booking_System.DTO.auth.LoginRequest;
import com.Sanchita.Resource_Booking_System.DTO.auth.LoginResponse;
import com.Sanchita.Resource_Booking_System.DTO.auth.UserRegistrationRequest;
import com.Sanchita.Resource_Booking_System.DTO.auth.UserRegistrationResponse;
import com.Sanchita.Resource_Booking_System.Entity.User;
import com.Sanchita.Resource_Booking_System.Enums.Role;
import com.Sanchita.Resource_Booking_System.Exception.EmailAlreadyExistsException;
import com.Sanchita.Resource_Booking_System.Repository.UserRepo;
import com.Sanchita.Resource_Booking_System.Security.JwtService;
import com.Sanchita.Resource_Booking_System.Services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {


    private final JwtService jwtService;
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserRegistrationResponse registerUser(UserRegistrationRequest request) {

        String email = request.getEmail().trim().toLowerCase();

        if (userRepo.existsByEmailIgnoreCase(email)) {
            throw new EmailAlreadyExistsException(
                    "Email is already registered"
            );
        }

        User user = User.builder()
                .name(request.getName().trim())
                .email(email)
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();

        User savedUser = userRepo.save(user);

        return UserRegistrationResponse.builder()
                .id(savedUser.getId())
                .name(savedUser.getName())
                .email(savedUser.getEmail())
                .role(savedUser.getRole())
                .build();
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        String email = request.getEmail().trim().toLowerCase();
        User user = userRepo.findByEmailIgnoreCase(email)
                .orElseThrow(() ->
                        new BadCredentialsException(
                                "Invalid email or password"
                        )
                );
        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword())) {

            throw new BadCredentialsException(
                    "Invalid email or password"
            );
        }

            String token = jwtService.generateToken(user);

            return LoginResponse.builder()
                    .token(token)
                    .tokenType("Bearer")
                    .userId(user.getId())
                    .role(user.getRole().name())
                    .build();
        }

}


