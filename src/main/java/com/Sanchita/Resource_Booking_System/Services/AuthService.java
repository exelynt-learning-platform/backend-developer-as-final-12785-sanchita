package com.Sanchita.Resource_Booking_System.Services;

import com.Sanchita.Resource_Booking_System.DTO.auth.LoginRequest;
import com.Sanchita.Resource_Booking_System.DTO.auth.LoginResponse;
import com.Sanchita.Resource_Booking_System.DTO.auth.UserRegistrationRequest;
import com.Sanchita.Resource_Booking_System.DTO.auth.UserRegistrationResponse;


public interface AuthService {

    UserRegistrationResponse registerUser(UserRegistrationRequest userRequest);

    LoginResponse login(LoginRequest request);
}
