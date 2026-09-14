package com.Sanchita.Resource_Booking_System.Controller;


import com.Sanchita.Resource_Booking_System.DTO.auth.LoginRequest;
import com.Sanchita.Resource_Booking_System.DTO.auth.LoginResponse;
import com.Sanchita.Resource_Booking_System.DTO.auth.UserRegistrationRequest;
import com.Sanchita.Resource_Booking_System.DTO.auth.UserRegistrationResponse;
import com.Sanchita.Resource_Booking_System.Services.AuthService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;


    @Test
    void register_shouldReturnCreated() throws Exception {
        UserRegistrationRequest request = new UserRegistrationRequest("Sanchita", "sanchita@gmail.com", "sanchita@123");
        UserRegistrationResponse response = new UserRegistrationResponse();
        Mockito.when(authService.registerUser(any(UserRegistrationRequest.class))).thenReturn(response);
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        { "name": "Sanchita", "email": "sanchita@gmail.com", "password": "sanchita@123" }
                        """))
                .andExpect(status().isCreated());
    }


    // Test Login API
     @Test
    void login_shouldReturnOk() throws Exception {
         LoginRequest request = new LoginRequest("sanchita@gmail.com", "sanchita@123");
         LoginResponse response = new LoginResponse();
         Mockito.when(authService.login(any(LoginRequest.class))).thenReturn(response);
         mockMvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON).content("""
                 { "email": "sanchita@gmail.com", "password": "sanchita@123" } """)).andExpect(status().isOk());
     }

}
