package com.Sanchita.Resource_Booking_System.Services.Impl;

import com.Sanchita.Resource_Booking_System.DTO.reservation.CancelReservationRequest;
import com.Sanchita.Resource_Booking_System.DTO.reservation.ReservationRequest;
import com.Sanchita.Resource_Booking_System.DTO.reservation.ReservationResponse;
import com.Sanchita.Resource_Booking_System.Entity.Reservation;
import com.Sanchita.Resource_Booking_System.Entity.Resource;
import com.Sanchita.Resource_Booking_System.Entity.User;
import com.Sanchita.Resource_Booking_System.Enums.ReservationStatus;
import com.Sanchita.Resource_Booking_System.Exception.*;
import com.Sanchita.Resource_Booking_System.Repository.ReservationRepo;
import com.Sanchita.Resource_Booking_System.Repository.ResourceRepo;
import com.Sanchita.Resource_Booking_System.Repository.UserRepo;
import com.Sanchita.Resource_Booking_System.Services.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepo reservationRepo;
    private final ResourceRepo resourceRepo;
    private final UserRepo userRepository;

    @Override
    public ReservationResponse createReservation(ReservationRequest request, Long userId) {
        // 1. Validate time
        if (!request.getEndTime()
                .isAfter(request.getStartTime())) {

            throw new InvalidReservationTimeException(
                    "End time must be after start time"
            );
        }

        // 2. Find resource
        Resource resource = resourceRepo
                .findById(request.getResourceId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Resource not found with id: "
                                        + request.getResourceId()
                        ));

        // 3. Check overlapping reservations
        List<ReservationStatus> activeStatuses =
                List.of(
                        ReservationStatus.PENDING,
                        ReservationStatus.CONFIRMED
                );

        boolean alreadyBooked =
                reservationRepo
                        .existsOverlappingReservation(
                                request.getResourceId(),
                                request.getStartTime(),
                                request.getEndTime(),
                                activeStatuses
                        );

        if (alreadyBooked) {
            throw new ReservationAlreadyExistsException(
                    "Resource is already booked for the selected time"
            );
        }

        // 4. Calculate price
        BigDecimal price = calculatePrice(resource, request.getStartTime(), request.getEndTime());

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found with id: " + userId));


        // 5. Create reservation
        Reservation reservation =
                Reservation.builder()
                        .resource(resource)
                        .user(user)
                        .startTime(request.getStartTime())
                        .endTime(request.getEndTime())
                        .price(price)
                        .status(ReservationStatus.PENDING)
                        .notes(request.getNotes())
                        .build();

        // 6. Save
        Reservation savedReservation =
                reservationRepo.save(reservation);

        return mapToResponse(savedReservation);
    }

    @Override
    public ReservationResponse getReservationById(Long reservationId, Long userId,  boolean isAdmin) {
        Reservation reservation = reservationRepo.findById(reservationId).orElseThrow(() -> new ReservationNotFoundException("Reservation not found with id: " + reservationId));

        if (!isAdmin && reservation.getUser().getId() != userId) {
            throw new ReservationAccessDeniedException(
                    "You are not authorized to view this reservation"
            );
        }
        return mapToResponse(reservation);
    }

    @Override
    public List<ReservationResponse> getMyReservations(Long userId) {
        return reservationRepo.findByUserId(userId).stream().map(this::mapToResponse).toList();
    }

    @Override
    public ReservationResponse cancelReservation(
            Long reservationId,
            Long userId,
            CancelReservationRequest request, boolean isAdmin) {

        // 1. Find reservation
        Reservation reservation = reservationRepo.findById(reservationId)
                .orElseThrow(() ->
                        new ReservationNotFoundException(
                                "Reservation not found with id: " + reservationId
                        ));

        // 2. Check ownership
        if (!isAdmin && reservation.getUser().getId() != userId) {
            throw new ReservationAccessDeniedException(
                    "You are not authorized to cancel this reservation"
            );
        }

        // 3. Check current status
        if (reservation.getStatus() == ReservationStatus.CANCELLED) {
            throw new ReservationCannotBeCancelledException(
                    "Reservation is already cancelled"
            );
        }

        // 4. Only PENDING reservation can be cancelled
        if (reservation.getStatus() == ReservationStatus.CONFIRMED) {
            throw new ReservationCannotBeCancelledException(
                    "Confirmed reservation cannot be cancelled"
            );
        }

        // 5. Cancel reservation
        reservation.setStatus(ReservationStatus.CANCELLED);
        reservation.setCancellationReason(
                request.getCancellationReason()
        );
        reservation.setCancelledAt(LocalDateTime.now());

        // 6. Save
        Reservation savedReservation =
                reservationRepo.save(reservation);

        // 7. Return response
        return mapToResponse(savedReservation);
    }
//    @Override
//    public List<ReservationResponse> getAllReservations() {
//        return reservationRepo.findAll().stream().map(this::mapToResponse).toList();
//    }

    @Override
    public Page<ReservationResponse> getAllReservations(ReservationStatus status, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {

        return reservationRepo.findReservations(status, minPrice, maxPrice, pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional
    public ReservationResponse updateReservation(Long reservationId, ReservationRequest request, Long userId, boolean isAdmin) {

        // 1. Validate reservation time
        if (!request.getEndTime().isAfter(request.getStartTime())) {
            throw new InvalidReservationTimeException("End time must be after start time");
        }

        // 2. Find existing reservation
        Reservation existingReservation = reservationRepo.findById(reservationId)
                .orElseThrow(() -> new ReservationNotFoundException("Reservation not found with id: " + reservationId));

        // 3. Check ownership
        if (!isAdmin && existingReservation.getUser().getId() != userId) {
            throw new ReservationAccessDeniedException(
                    "You are not allowed to update this reservation"
            );
        }

        // 4. Confirmed reservation cannot be updated
        if (existingReservation.getStatus() == ReservationStatus.CONFIRMED) {
            throw new ReservationCannotBeUpdatedException("Confirmed reservation cannot be updated");
        }

        // 5. Cancelled reservation cannot be updated
        if (existingReservation.getStatus() == ReservationStatus.CANCELLED) {
            throw new ReservationCannotBeUpdatedException("Cancelled reservation cannot be updated");
        }

        // 6. Find resource
        Resource resource = resourceRepo.findById(request.getResourceId())
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found with id: " + request.getResourceId()));

        // 7. Active statuses which block the requested time
        List<ReservationStatus> activeStatuses =
                List.of(ReservationStatus.PENDING, ReservationStatus.CONFIRMED);

        // 8. Check overlapping reservation
        boolean alreadyBooked =
                reservationRepo.existsOverlappingReservationForUpdate(
                        reservationId,
                        request.getResourceId(),
                        request.getStartTime(),
                        request.getEndTime(),
                        activeStatuses
                );

        if (alreadyBooked) {
            throw new ReservationAlreadyExistsException(
                    "Resource is already booked for the selected time"
            );
        }

        // 9. Recalculate price based on new time
        BigDecimal price = calculatePrice(resource, request.getStartTime(), request.getEndTime());

        // 10. Update reservation
        existingReservation.setResource(resource);
        existingReservation.setStartTime(request.getStartTime());
        existingReservation.setEndTime(request.getEndTime());
        existingReservation.setPrice(price);
        existingReservation.setNotes(request.getNotes());

        // 11. Save
        Reservation savedReservation = reservationRepo.save(existingReservation);

        // 12. Convert to response
        return mapToResponse(savedReservation);
    }



    @Override
    @Transactional
    public ReservationResponse confirmReservation(Long reservationId) {

        Reservation reservation = reservationRepo.findById(reservationId)
                .orElseThrow(() -> new ReservationNotFoundException("Reservation not found with id: " + reservationId));

        // Already cancelled
        if (reservation.getStatus() == ReservationStatus.CANCELLED) {
            throw new ReservationCannotBeConfirmedException("Cancelled reservation cannot be confirmed");
        }

        // Already confirmed
        if (reservation.getStatus() == ReservationStatus.CONFIRMED) {
            throw new ReservationCannotBeConfirmedException("Reservation is already confirmed");
        }

        // Only PENDING reservation can be confirmed
        if (reservation.getStatus() != ReservationStatus.PENDING) {
            throw new ReservationCannotBeConfirmedException("Only PENDING reservations can be confirmed");
        }

        reservation.setApprovedAt(LocalDateTime.now());

        // Change status
        reservation.setStatus(ReservationStatus.CONFIRMED);

        // Save
        Reservation savedReservation = reservationRepo.save(reservation);

        return mapToResponse(savedReservation);
    }

    private BigDecimal calculatePrice(Resource resource, LocalDateTime startTime, LocalDateTime endTime) {

        long minutes = Duration.between(startTime, endTime).toMinutes();

        BigDecimal hours = BigDecimal.valueOf(minutes).divide(BigDecimal.valueOf(60), 2, java.math.RoundingMode.UP);

        return resource.getPricePerHour().multiply(hours);
    }

    private ReservationResponse mapToResponse(
            Reservation reservation) {

        return ReservationResponse.builder()
                .id(reservation.getId())
                .reservationNumber(reservation.getReservationNumber())
                .resourceId(reservation.getResource().getId())
                .resourceName(reservation.getResource().getName())
                .startTime(reservation.getStartTime())
                .endTime(reservation.getEndTime())
                .price(reservation.getPrice())
                .status(reservation.getStatus())
                .notes(reservation.getNotes())
                .cancellationReason(reservation.getCancellationReason())
                .cancelledAt(reservation.getCancelledAt())
                .createdAt(reservation.getCreatedAt())
                .updatedAt(reservation.getUpdatedAt())
                .build();

}

    @Override
    @Transactional
    public void deleteReservation(Long reservationId) {

        Reservation reservation = reservationRepo.findById(reservationId)
                .orElseThrow(() ->
                        new ReservationNotFoundException(
                                "Reservation not found with id: " + reservationId
                        ));

        reservationRepo.delete(reservation);
    }

}
