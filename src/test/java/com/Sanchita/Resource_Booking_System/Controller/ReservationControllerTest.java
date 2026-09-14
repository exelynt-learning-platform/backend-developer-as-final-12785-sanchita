package com.Sanchita.Resource_Booking_System.Controller;

import com.Sanchita.Resource_Booking_System.DTO.reservation.CancelReservationRequest;
import com.Sanchita.Resource_Booking_System.DTO.reservation.ReservationRequest;
import com.Sanchita.Resource_Booking_System.DTO.reservation.ReservationResponse;
import com.Sanchita.Resource_Booking_System.Services.ReservationService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReservationController.class)
class ReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReservationService reservationService;


    @Test
    @WithMockUser(username = "1")
    void createReservation() throws Exception {

        Mockito.when(reservationService.createReservation(
                any(ReservationRequest.class),
                eq(1L)
        )).thenReturn(new ReservationResponse());

        mockMvc.perform(
                post("/reservation/request/createReservation")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "resourceId": 1,
                                    "startTime": "2030-12-20T10:00:00",
                                    "endTime": "2030-12-20T12:00:00",
                                    "notes": "Meeting"
                                }
                                """)
        ).andExpect(status().isCreated());
    }


    @Test
    @WithMockUser(username = "1")
    void getMyReservations() throws Exception {

        Mockito.when(reservationService.getMyReservations(1L))
                .thenReturn(List.of(new ReservationResponse()));

        mockMvc.perform(
                get("/reservation/request/getMyReservation")
        ).andExpect(status().isOk());
    }


    @Test
    @WithMockUser(username = "1")
    void getReservationById() throws Exception {

        Mockito.when(reservationService.getReservationById(
                1L,
                1L,
                false
        )).thenReturn(new ReservationResponse());

        mockMvc.perform(
                get("/reservation/request/getReservationById/1")
        ).andExpect(status().isOk());
    }


    @Test
    @WithMockUser(username = "1")
    void cancelReservation() throws Exception {

        Mockito.when(reservationService.cancelReservation(
                eq(1L),
                eq(1L),
                any(CancelReservationRequest.class),
                eq(false)
        )).thenReturn(new ReservationResponse());

        mockMvc.perform(
                patch("/reservation/request/cancelReservation/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "cancellationReason": "Personal reason"
                                }
                                """)
        ).andExpect(status().isOk());
    }


    @Test
    @WithMockUser(username = "1")
    void getAllReservations() throws Exception {

        Page<ReservationResponse> page =
                new PageImpl<>(
                        List.of(new ReservationResponse()),
                        PageRequest.of(0, 5),
                        1
                );

        Mockito.when(reservationService.getAllReservations(
                isNull(),
                isNull(),
                isNull(),
                any(Pageable.class)
        )).thenReturn(page);

        mockMvc.perform(
                get("/reservation/getAllReservations")
        ).andExpect(status().isOk());
    }


    @Test
    @WithMockUser(username = "1")
    void updateReservation() throws Exception {

        Mockito.when(reservationService.updateReservation(
                eq(1L),
                any(ReservationRequest.class),
                eq(1L),
                eq(false)
        )).thenReturn(new ReservationResponse());

        mockMvc.perform(
                put("/reservation/request/updateReservation/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "resourceId": 1,
                                    "startTime": "2030-12-20T10:00:00",
                                    "endTime": "2030-12-20T12:00:00",
                                    "notes": "Updated reservation"
                                }
                                """)
        ).andExpect(status().isOk());
    }


    @Test
    @WithMockUser(username = "1", roles = "ADMIN")
    void confirmReservation() throws Exception {

        Mockito.when(reservationService.confirmReservation(1L))
                .thenReturn(new ReservationResponse());

        mockMvc.perform(
                patch("/reservation/confirmReservation/1")
                        .with(csrf())
        ).andExpect(status().isOk());
    }
}