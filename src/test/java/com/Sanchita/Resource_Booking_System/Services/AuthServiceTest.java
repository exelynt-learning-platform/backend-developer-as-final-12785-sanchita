package com.Sanchita.Resource_Booking_System.Services;

import com.Sanchita.Resource_Booking_System.DTO.auth.LoginRequest;
import com.Sanchita.Resource_Booking_System.DTO.auth.LoginResponse;
import com.Sanchita.Resource_Booking_System.DTO.auth.UserRegistrationRequest;
import com.Sanchita.Resource_Booking_System.DTO.auth.UserRegistrationResponse;
import com.Sanchita.Resource_Booking_System.Entity.User;
import com.Sanchita.Resource_Booking_System.Enums.Role;
import com.Sanchita.Resource_Booking_System.Exception.EmailAlreadyExistsException;
import com.Sanchita.Resource_Booking_System.Repository.UserRepo;
import com.Sanchita.Resource_Booking_System.Security.JwtService;
import com.Sanchita.Resource_Booking_System.Services.Impl.AuthServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepo userRepo;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthServiceImpl authService;


    // ============================================================
    // REGISTER USER
    // ============================================================

    @Test
    void registerUser_shouldRegisterSuccessfully() {

        // Arrange
        UserRegistrationRequest request =
                new UserRegistrationRequest(
                        " Sanchita ",
                        " sanchita@gmail.com ",
                        "sanchita@123"
                );

        User savedUser = User.builder()
                .id(1L)
                .name("Sanchita")
                .email("sanchita@gmail.com")
                .password("encodedPassword")
                .role(Role.USER)
                .build();

        when(userRepo.existsByEmailIgnoreCase("sanchita@gmail.com"))
                .thenReturn(false);

        when(passwordEncoder.encode("sanchita@123"))
                .thenReturn("encodedPassword");

        when(userRepo.save(any(User.class)))
                .thenReturn(savedUser);


        // Act
        UserRegistrationResponse response =
                authService.registerUser(request);


        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Sanchita", response.getName());
        assertEquals("sanchita@gmail.com", response.getEmail());
        assertEquals(Role.USER, response.getRole());

        verify(userRepo)
                .existsByEmailIgnoreCase("sanchita@gmail.com");

        verify(passwordEncoder)
                .encode("sanchita@123");

        verify(userRepo)
                .save(any(User.class));
    }


    @Test
    void registerUser_shouldThrowException_whenEmailAlreadyExists() {

        // Arrange
        UserRegistrationRequest request =
                new UserRegistrationRequest(
                        "Sanchita",
                        "sanchita@gmail.com",
                        "sanchita@123"
                );

        when(userRepo.existsByEmailIgnoreCase("sanchita@gmail.com"))
                .thenReturn(true);


        // Act & Assert
        assertThrows(
                EmailAlreadyExistsException.class,
                () -> authService.registerUser(request)
        );


        // Verify
        verify(userRepo)
                .existsByEmailIgnoreCase("sanchita@gmail.com");

        verify(userRepo, never())
                .save(any(User.class));

        verify(passwordEncoder, never())
                .encode(anyString());
    }


    // ============================================================
    // LOGIN
    // ============================================================

    @Test
    void login_shouldReturnLoginResponse_whenCredentialsAreValid() {

        // Arrange
        LoginRequest request =
                new LoginRequest(
                        " sanchita@gmail.com ",
                        "sanchita@123"
                );

        User user = User.builder()
                .id(1L)
                .name("Sanchita")
                .email("sanchita@gmail.com")
                .password("encodedPassword")
                .role(Role.USER)
                .build();

        when(userRepo.findByEmailIgnoreCase("sanchita@gmail.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(
                "sanchita@123",
                "encodedPassword"
        )).thenReturn(true);

        when(jwtService.generateToken(user))
                .thenReturn("jwt-token");


        // Act
        LoginResponse response =
                authService.login(request);


        // Assert
        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());
        assertEquals("Bearer", response.getTokenType());
        assertEquals(1L, response.getUserId());
        assertEquals("USER", response.getRole());

        verify(userRepo)
                .findByEmailIgnoreCase("sanchita@gmail.com");

        verify(passwordEncoder)
                .matches(
                        "sanchita@123",
                        "encodedPassword"
                );

        verify(jwtService)
                .generateToken(user);
    }


    @Test
    void login_shouldThrowException_whenEmailDoesNotExist() {

        // Arrange
        LoginRequest request =
                new LoginRequest(
                        "unknown@gmail.com",
                        "password123"
                );

        when(userRepo.findByEmailIgnoreCase("unknown@gmail.com"))
                .thenReturn(Optional.empty());


        // Act & Assert
        assertThrows(
                BadCredentialsException.class,
                () -> authService.login(request)
        );


        // Verify
        verify(userRepo)
                .findByEmailIgnoreCase("unknown@gmail.com");

        verify(passwordEncoder, never())
                .matches(anyString(), anyString());

        verify(jwtService, never())
                .generateToken(any(User.class));
    }


    @Test
    void login_shouldThrowException_whenPasswordIsIncorrect() {

        // Arrange
        LoginRequest request =
                new LoginRequest(
                        "sanchita@gmail.com",
                        "wrongPassword"
                );

        User user = User.builder()
                .id(1L)
                .name("Sanchita")
                .email("sanchita@gmail.com")
                .password("encodedPassword")
                .role(Role.USER)
                .build();

        when(userRepo.findByEmailIgnoreCase("sanchita@gmail.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(
                "wrongPassword",
                "encodedPassword"
        )).thenReturn(false);


        // Act & Assert
        assertThrows(
                BadCredentialsException.class,
                () -> authService.login(request)
        );


        // Verify
        verify(userRepo)
                .findByEmailIgnoreCase("sanchita@gmail.com");

        verify(passwordEncoder)
                .matches(
                        "wrongPassword",
                        "encodedPassword"
                );

        verify(jwtService, never())
                .generateToken(any(User.class));
    }
}