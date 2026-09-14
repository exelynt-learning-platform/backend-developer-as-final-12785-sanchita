package com.Sanchita.Resource_Booking_System.Controller;

import com.Sanchita.Resource_Booking_System.DTO.reservation.CancelReservationRequest;
import com.Sanchita.Resource_Booking_System.DTO.reservation.PageResponse;
import com.Sanchita.Resource_Booking_System.DTO.reservation.ReservationRequest;
import com.Sanchita.Resource_Booking_System.DTO.reservation.ReservationResponse;
import com.Sanchita.Resource_Booking_System.Enums.ReservationStatus;
import com.Sanchita.Resource_Booking_System.Services.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/reservation")
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping("/request/createReservation")
    public ResponseEntity<ReservationResponse> createReservation(@Valid @RequestBody ReservationRequest request, Authentication authentication) {

        Long userId = Long.valueOf(authentication.getName());

        ReservationResponse response = reservationService.createReservation(request, userId);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/request/getMyReservation")
    public ResponseEntity<List<ReservationResponse>> getMyReservations(Authentication authentication) {

        Long userId = Long.valueOf(authentication.getName());

        return ResponseEntity.ok(reservationService.getMyReservations(userId));
    }

    // GET ONE RESERVATION
    @GetMapping("/request/getReservationById/{id}")
    public ResponseEntity<ReservationResponse> getReservationById(@PathVariable Long id, Authentication authentication) {

        Long userId = Long.valueOf(authentication.getName());

        boolean isAdmin = authentication.getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        return ResponseEntity.ok(
                reservationService.getReservationById(id, userId, isAdmin));
    }

    // CANCEL RESERVATION
    @PatchMapping("/request/cancelReservation/{id}")
    public ResponseEntity<ReservationResponse> cancelReservation(@PathVariable Long id, @Valid @RequestBody CancelReservationRequest request, Authentication authentication) {

        Long userId = Long.valueOf(authentication.getName());

        boolean isAdmin = authentication.getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        ReservationResponse response = reservationService.cancelReservation(id, userId, request, isAdmin);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/getAllReservations")
    public ResponseEntity<PageResponse<ReservationResponse>> getAllReservations(@RequestParam(required = false) ReservationStatus status,
                                                                                @RequestParam(required = false) BigDecimal minPrice,
                                                                                @RequestParam(required = false) BigDecimal maxPrice,
                                                                                @RequestParam(defaultValue = "0") int page,
                                                                                @RequestParam(defaultValue = "5") int size,
                                                                                @RequestParam(defaultValue = "createdAt") String sortBy,
                                                                                @RequestParam(defaultValue = "desc") String sortDirection) {

        Sort.Direction direction = sortDirection.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<ReservationResponse> result =
                reservationService.getAllReservations(
                        status, minPrice, maxPrice, pageable);

        PageResponse<ReservationResponse> response =
                PageResponse.<ReservationResponse>builder()
                        .content(result.getContent())
                        .page(result.getNumber())
                        .size(result.getSize())
                        .totalElements(result.getTotalElements())
                        .totalPages(result.getTotalPages())
                        .first(result.isFirst())
                        .last(result.isLast())
                        .build();

        return ResponseEntity.ok(response);
    }


    @PutMapping("/request/updateReservation/{id}")
    public ResponseEntity<ReservationResponse> updateReservation(@PathVariable Long id, @Valid @RequestBody ReservationRequest request, Authentication authentication) {

        Long userId = Long.valueOf(authentication.getName());

        boolean isAdmin = authentication.getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        return ResponseEntity.ok(reservationService.updateReservation(id, request, userId, isAdmin));
    }


    @PatchMapping("/confirmReservation/{id}")
    public ResponseEntity<ReservationResponse> confirmReservation( @PathVariable Long id)
    {
        ReservationResponse response = reservationService.confirmReservation(id);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/deleteReservation/{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable Long id) {

        reservationService.deleteReservation(id);

        return ResponseEntity.noContent().build();
    }
}
