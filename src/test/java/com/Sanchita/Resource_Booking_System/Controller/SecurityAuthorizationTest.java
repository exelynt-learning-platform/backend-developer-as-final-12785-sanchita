package com.Sanchita.Resource_Booking_System.Controller;

import com.Sanchita.Resource_Booking_System.DTO.resources.ResourceRequest;
import com.Sanchita.Resource_Booking_System.DTO.resources.ResourceResponse;
import com.Sanchita.Resource_Booking_System.Services.ResourceService;
import com.Sanchita.Resource_Booking_System.Services.ReservationService;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = {
        ResourceController.class,
        ReservationController.class
})
@Import(SecurityAuthorizationTest.TestSecurityConfig.class)
class SecurityAuthorizationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ResourceService resourceService;

    @MockitoBean
    private ReservationService reservationService;


    // =========================================================
    // TEST SECURITY CONFIGURATION
    // =========================================================

    @TestConfiguration
    static class TestSecurityConfig {

        @Bean
        SecurityFilterChain testSecurityFilterChain(HttpSecurity http)
                throws Exception {

            http
                    .csrf(csrf -> csrf.disable())

                    .authorizeHttpRequests(auth -> auth

                            // USER + ADMIN can GET resources
                            .requestMatchers(
                                    HttpMethod.GET,
                                    "/resources/**"
                            ).hasAnyRole("USER", "ADMIN")

                            // Only ADMIN can modify resources
                            .requestMatchers(
                                    "/resources/**"
                            ).hasRole("ADMIN")

                            // Only ADMIN can confirm reservation
                            .requestMatchers(
                                    "/reservation/confirmReservation/**"
                            ).hasRole("ADMIN")

                            // All other requests require authentication
                            .anyRequest()
                            .authenticated()
                    );

            return http.build();
        }
    }


    // =========================================================
    // USER TESTS
    // =========================================================

    @Test
    @WithMockUser(username = "1", roles = "USER")
    void userCanGetResources() throws Exception {

        mockMvc.perform(
                        get("/resources/getResource")
                )
                .andExpect(status().isOk());
    }


    @Test
    @WithMockUser(username = "1", roles = "USER")
    void userCannotCreateResource() throws Exception {

        mockMvc.perform(
                        post("/resources/createResource")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                {
                                    "name": "Conference Room A",
                                    "description": "Meeting room",
                                    "location": "Pune Office",
                                    "resourceTypeId": 1,
                                    "pricePerHour": 500.00,
                                    "active": true
                                }
                                """)
                )
                .andExpect(status().isForbidden());
    }


    @Test
    @WithMockUser(username = "1", roles = "USER")
    void userCannotDeleteResource() throws Exception {

        mockMvc.perform(
                        delete("/resources/deleteResource/1")
                )
                .andExpect(status().isForbidden());
    }


    @Test
    @WithMockUser(username = "1", roles = "USER")
    void userCannotConfirmReservation() throws Exception {

        mockMvc.perform(
                        patch("/reservation/confirmReservation/1")
                )
                .andExpect(status().isForbidden());
    }


    // =========================================================
    // ADMIN TESTS
    // =========================================================

    @Test
    @WithMockUser(username = "1", roles = "ADMIN")
    void adminCanCreateResource() throws Exception {

        Mockito.when(
                resourceService.createResource(
                        any(ResourceRequest.class)
                )
        ).thenReturn(new ResourceResponse());


        mockMvc.perform(
                        post("/resources/createResource")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                {
                                    "name": "Conference Room A",
                                    "description": "Meeting room",
                                    "location": "Pune Office",
                                    "resourceTypeId": 1,
                                    "pricePerHour": 500.00,
                                    "active": true
                                }
                                """)
                )
                .andExpect(status().isCreated());
    }


    @Test
    @WithMockUser(username = "1", roles = "ADMIN")
    void adminCanDeleteResource() throws Exception {

        mockMvc.perform(
                        delete("/resources/deleteResource/1")
                )
                .andExpect(status().isNoContent());
    }


    @Test
    @WithMockUser(username = "1", roles = "ADMIN")
    void adminCanConfirmReservation() throws Exception {

        Mockito.when(
                reservationService.confirmReservation(1L)
        ).thenReturn(null);


        mockMvc.perform(
                        patch("/reservation/confirmReservation/1")
                )
                .andExpect(status().isOk());
    }


}