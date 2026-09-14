package com.Sanchita.Resource_Booking_System.Services;

import com.Sanchita.Resource_Booking_System.DTO.reservation.CancelReservationRequest;
import com.Sanchita.Resource_Booking_System.DTO.reservation.ReservationRequest;
import com.Sanchita.Resource_Booking_System.DTO.reservation.ReservationResponse;
import com.Sanchita.Resource_Booking_System.Enums.ReservationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;


public interface ReservationService {

    ReservationResponse createReservation(ReservationRequest request, Long userId);

    ReservationResponse getReservationById(Long reservationId, Long userId, boolean isAdmin);

    List<ReservationResponse> getMyReservations(Long userId);

    ReservationResponse cancelReservation(Long reservationId, Long userId, CancelReservationRequest request, boolean isAdmin);

    Page<ReservationResponse> getAllReservations(ReservationStatus status, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable );

    ReservationResponse updateReservation(Long reservationId, ReservationRequest request, Long userId, boolean isAdmin);

    ReservationResponse confirmReservation(Long reservationId);

    void deleteReservation(Long reservationId);
}
