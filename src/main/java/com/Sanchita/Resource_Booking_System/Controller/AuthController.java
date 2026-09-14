package com.Sanchita.Resource_Booking_System.Controller;

import com.Sanchita.Resource_Booking_System.DTO.auth.LoginRequest;
import com.Sanchita.Resource_Booking_System.DTO.auth.LoginResponse;
import com.Sanchita.Resource_Booking_System.DTO.auth.UserRegistrationRequest;
import com.Sanchita.Resource_Booking_System.DTO.auth.UserRegistrationResponse;
import com.Sanchita.Resource_Booking_System.Entity.User;
import com.Sanchita.Resource_Booking_System.Services.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

     @PostMapping("/register")
    public ResponseEntity<UserRegistrationResponse> register(@Valid @RequestBody UserRegistrationRequest userRequest){

        UserRegistrationResponse user = authService.registerUser(userRequest);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(user);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest loginRequest) {

        LoginResponse response =
                authService.login(loginRequest);

        return ResponseEntity.ok(response);
    }
}
