package com.Sanchita.Resource_Booking_System.Services;

import com.Sanchita.Resource_Booking_System.DTO.reservation.CancelReservationRequest;
import com.Sanchita.Resource_Booking_System.DTO.reservation.ReservationRequest;
import com.Sanchita.Resource_Booking_System.DTO.reservation.ReservationResponse;
import com.Sanchita.Resource_Booking_System.Entity.Reservation;
import com.Sanchita.Resource_Booking_System.Entity.Resource;
import com.Sanchita.Resource_Booking_System.Entity.User;
import com.Sanchita.Resource_Booking_System.Enums.ReservationStatus;
import com.Sanchita.Resource_Booking_System.Exception.ReservationAccessDeniedException;
import com.Sanchita.Resource_Booking_System.Exception.ReservationAlreadyExistsException;
import com.Sanchita.Resource_Booking_System.Exception.ReservationCannotBeCancelledException;
import com.Sanchita.Resource_Booking_System.Exception.ReservationCannotBeConfirmedException;
import com.Sanchita.Resource_Booking_System.Exception.ReservationCannotBeUpdatedException;
import com.Sanchita.Resource_Booking_System.Exception.ResourceNotFoundException;
import com.Sanchita.Resource_Booking_System.Repository.ReservationRepo;
import com.Sanchita.Resource_Booking_System.Repository.ResourceRepo;
import com.Sanchita.Resource_Booking_System.Repository.UserRepo;
import com.Sanchita.Resource_Booking_System.Services.Impl.ReservationServiceImpl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock
    private ReservationRepo reservationRepo;

    @Mock
    private ResourceRepo resourceRepo;

    @Mock
    private UserRepo userRepository;

    @InjectMocks
    private ReservationServiceImpl reservationService;


    // =========================================================
    // CREATE RESERVATION
    // =========================================================

    @Test
    void createReservation_shouldCreateSuccessfully() {

        LocalDateTime start =
                LocalDateTime.of(2030, 12, 20, 10, 0);

        LocalDateTime end =
                LocalDateTime.of(2030, 12, 20, 12, 0);

        ReservationRequest request = new ReservationRequest();

        request.setResourceId(1L);
        request.setStartTime(start);
        request.setEndTime(end);
        request.setNotes("Meeting");

        Resource resource = Resource.builder()
                .id(1L)
                .name("Conference Room")
                .pricePerHour(new BigDecimal("500.00"))
                .build();

        User user = User.builder()
                .id(1L)
                .name("Sanchita")
                .email("sanchita@gmail.com")
                .build();

        Reservation reservation = Reservation.builder()
                .id(1L)
                .resource(resource)
                .user(user)
                .startTime(start)
                .endTime(end)
                .price(new BigDecimal("1000.00"))
                .status(ReservationStatus.PENDING)
                .notes("Meeting")
                .build();

        when(resourceRepo.findById(1L))
                .thenReturn(Optional.of(resource));

        when(reservationRepo.existsOverlappingReservation(
                eq(1L),
                eq(start),
                eq(end),
                anyList()
        )).thenReturn(false);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(reservationRepo.save(any(Reservation.class)))
                .thenReturn(reservation);

        ReservationResponse response =
                reservationService.createReservation(request, 1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Conference Room", response.getResourceName());
        assertEquals(ReservationStatus.PENDING, response.getStatus());

        assertEquals(
                0,
                new BigDecimal("1000.00")
                        .compareTo(response.getPrice())
        );

        verify(reservationRepo).save(any(Reservation.class));
    }


    @Test
    void createReservation_shouldThrowException_whenResourceNotFound() {

        ReservationRequest request = new ReservationRequest();

        request.setResourceId(1L);
        request.setStartTime(
                LocalDateTime.of(2030, 12, 20, 10, 0)
        );
        request.setEndTime(
                LocalDateTime.of(2030, 12, 20, 12, 0)
        );

        when(resourceRepo.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> reservationService.createReservation(request, 1L)
        );

        verify(reservationRepo, never())
                .save(any());
    }


    @Test
    void createReservation_shouldThrowException_whenAlreadyBooked() {

        LocalDateTime start =
                LocalDateTime.of(2030, 12, 20, 10, 0);

        LocalDateTime end =
                LocalDateTime.of(2030, 12, 20, 12, 0);

        ReservationRequest request = new ReservationRequest();

        request.setResourceId(1L);
        request.setStartTime(start);
        request.setEndTime(end);

        Resource resource = Resource.builder()
                .id(1L)
                .name("Conference Room")
                .pricePerHour(new BigDecimal("500.00"))
                .build();

        when(resourceRepo.findById(1L))
                .thenReturn(Optional.of(resource));

        when(reservationRepo.existsOverlappingReservation(
                eq(1L),
                eq(start),
                eq(end),
                anyList()
        )).thenReturn(true);

        assertThrows(
                ReservationAlreadyExistsException.class,
                () -> reservationService.createReservation(request, 1L)
        );

        verify(reservationRepo, never())
                .save(any());
    }


    // =========================================================
    // GET RESERVATION
    // =========================================================

    @Test
    void getReservationById_shouldReturnReservation() {

        User user = User.builder()
                .id(1L)
                .build();

        Resource resource = Resource.builder()
                .id(1L)
                .name("Conference Room")
                .build();

        Reservation reservation = Reservation.builder()
                .id(1L)
                .user(user)
                .resource(resource)
                .status(ReservationStatus.PENDING)
                .build();

        when(reservationRepo.findById(1L))
                .thenReturn(Optional.of(reservation));

        ReservationResponse response =
                reservationService.getReservationById(
                        1L,
                        1L,
                        false
                );

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(
                "Conference Room",
                response.getResourceName()
        );
    }


    @Test
    void getReservationById_shouldThrowException_whenNotOwner() {

        User user = User.builder()
                .id(2L)
                .build();

        Reservation reservation = Reservation.builder()
                .id(1L)
                .user(user)
                .status(ReservationStatus.PENDING)
                .build();

        when(reservationRepo.findById(1L))
                .thenReturn(Optional.of(reservation));

        assertThrows(
                ReservationAccessDeniedException.class,
                () -> reservationService.getReservationById(
                        1L,
                        1L,
                        false
                )
        );
    }


    // =========================================================
    // GET MY RESERVATIONS
    // =========================================================

    @Test
    void getMyReservations_shouldReturnReservations() {

        User user = User.builder()
                .id(1L)
                .build();

        Resource resource = Resource.builder()
                .id(1L)
                .name("Conference Room")
                .build();

        Reservation reservation = Reservation.builder()
                .id(1L)
                .user(user)
                .resource(resource)
                .status(ReservationStatus.PENDING)
                .build();

        when(reservationRepo.findByUserId(1L))
                .thenReturn(List.of(reservation));

        List<ReservationResponse> response =
                reservationService.getMyReservations(1L);

        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals(1L, response.get(0).getId());

        verify(reservationRepo)
                .findByUserId(1L);
    }


    // =========================================================
    // CANCEL RESERVATION
    // =========================================================

    @Test
    void cancelReservation_shouldCancelSuccessfully() {

        User user = User.builder()
                .id(1L)
                .build();

        Resource resource = Resource.builder()
                .id(1L)
                .name("Conference Room")
                .build();

        Reservation reservation = Reservation.builder()
                .id(1L)
                .user(user)
                .resource(resource)
                .status(ReservationStatus.PENDING)
                .build();

        CancelReservationRequest request =
                new CancelReservationRequest();

        request.setCancellationReason("Personal reason");

        when(reservationRepo.findById(1L))
                .thenReturn(Optional.of(reservation));

        when(reservationRepo.save(reservation))
                .thenReturn(reservation);

        ReservationResponse response =
                reservationService.cancelReservation(
                        1L,
                        1L,
                        request,
                        false
                );

        assertNotNull(response);

        assertEquals(
                ReservationStatus.CANCELLED,
                reservation.getStatus()
        );

        assertEquals(
                "Personal reason",
                reservation.getCancellationReason()
        );

        assertNotNull(
                reservation.getCancelledAt()
        );

        verify(reservationRepo)
                .save(reservation);
    }


    @Test
    void cancelReservation_shouldThrowException_whenConfirmed() {

        User user = User.builder()
                .id(1L)
                .build();

        Reservation reservation = Reservation.builder()
                .id(1L)
                .user(user)
                .status(ReservationStatus.CONFIRMED)
                .build();

        CancelReservationRequest request =
                new CancelReservationRequest();

        request.setCancellationReason("Personal reason");

        when(reservationRepo.findById(1L))
                .thenReturn(Optional.of(reservation));

        assertThrows(
                ReservationCannotBeCancelledException.class,
                () -> reservationService.cancelReservation(
                        1L,
                        1L,
                        request,
                        false
                )
        );

        verify(reservationRepo, never())
                .save(any());
    }


    // =========================================================
    // GET ALL RESERVATIONS
    // =========================================================

    @Test
    void getAllReservations_shouldReturnReservations() {

        PageRequest pageable =
                PageRequest.of(0, 5);

        User user = User.builder()
                .id(1L)
                .build();

        Resource resource = Resource.builder()
                .id(1L)
                .name("Conference Room")
                .build();

        Reservation reservation = Reservation.builder()
                .id(1L)
                .user(user)
                .resource(resource)
                .status(ReservationStatus.PENDING)
                .build();

        Page<Reservation> page =
                new PageImpl<>(List.of(reservation));

        when(reservationRepo.findReservations(
                isNull(),
                isNull(),
                isNull(),
                eq(pageable)
        )).thenReturn(page);

        Page<ReservationResponse> response =
                reservationService.getAllReservations(
                        null,
                        null,
                        null,
                        pageable
                );

        assertNotNull(response);
        assertEquals(
                1,
                response.getTotalElements()
        );

        assertEquals(
                1L,
                response.getContent()
                        .get(0)
                        .getId()
        );

        verify(reservationRepo)
                .findReservations(
                        null,
                        null,
                        null,
                        pageable
                );
    }


    // =========================================================
    // UPDATE RESERVATION
    // =========================================================

    @Test
    void updateReservation_shouldUpdateSuccessfully() {

        LocalDateTime start =
                LocalDateTime.of(2030, 12, 20, 10, 0);

        LocalDateTime end =
                LocalDateTime.of(2030, 12, 20, 12, 0);

        ReservationRequest request =
                new ReservationRequest();

        request.setResourceId(1L);
        request.setStartTime(start);
        request.setEndTime(end);
        request.setNotes("Updated meeting");

        User user = User.builder()
                .id(1L)
                .build();

        Resource resource = Resource.builder()
                .id(1L)
                .name("Conference Room")
                .pricePerHour(new BigDecimal("500.00"))
                .build();

        Reservation reservation = Reservation.builder()
                .id(1L)
                .user(user)
                .resource(resource)
                .status(ReservationStatus.PENDING)
                .build();

        when(reservationRepo.findById(1L))
                .thenReturn(Optional.of(reservation));

        when(resourceRepo.findById(1L))
                .thenReturn(Optional.of(resource));

        when(reservationRepo.existsOverlappingReservationForUpdate(
                eq(1L),
                eq(1L),
                eq(start),
                eq(end),
                anyList()
        )).thenReturn(false);

        when(reservationRepo.save(reservation))
                .thenReturn(reservation);

        ReservationResponse response =
                reservationService.updateReservation(
                        1L,
                        request,
                        1L,
                        false
                );

        assertNotNull(response);

        assertEquals(
                "Updated meeting",
                reservation.getNotes()
        );

        assertEquals(
                0,
                new BigDecimal("1000.00")
                        .compareTo(response.getPrice())
        );

        verify(reservationRepo)
                .save(reservation);
    }


    @Test
    void updateReservation_shouldThrowException_whenConfirmed() {

        User user = User.builder()
                .id(1L)
                .build();

        Reservation reservation = Reservation.builder()
                .id(1L)
                .user(user)
                .status(ReservationStatus.CONFIRMED)
                .build();

        ReservationRequest request =
                new ReservationRequest();

        request.setResourceId(1L);
        request.setStartTime(
                LocalDateTime.of(2030, 12, 20, 10, 0)
        );
        request.setEndTime(
                LocalDateTime.of(2030, 12, 20, 12, 0)
        );

        when(reservationRepo.findById(1L))
                .thenReturn(Optional.of(reservation));

        assertThrows(
                ReservationCannotBeUpdatedException.class,
                () -> reservationService.updateReservation(
                        1L,
                        request,
                        1L,
                        false
                )
        );
    }


    @Test
    void updateReservation_shouldThrowException_whenAlreadyBooked() {

        LocalDateTime start =
                LocalDateTime.of(2030, 12, 20, 10, 0);

        LocalDateTime end =
                LocalDateTime.of(2030, 12, 20, 12, 0);

        ReservationRequest request =
                new ReservationRequest();

        request.setResourceId(1L);
        request.setStartTime(start);
        request.setEndTime(end);

        User user = User.builder()
                .id(1L)
                .build();

        Resource resource = Resource.builder()
                .id(1L)
                .name("Conference Room")
                .pricePerHour(new BigDecimal("500.00"))
                .build();

        Reservation reservation = Reservation.builder()
                .id(1L)
                .user(user)
                .resource(resource)
                .status(ReservationStatus.PENDING)
                .build();

        when(reservationRepo.findById(1L))
                .thenReturn(Optional.of(reservation));

        when(resourceRepo.findById(1L))
                .thenReturn(Optional.of(resource));

        when(reservationRepo.existsOverlappingReservationForUpdate(
                eq(1L),
                eq(1L),
                eq(start),
                eq(end),
                anyList()
        )).thenReturn(true);

        assertThrows(
                ReservationAlreadyExistsException.class,
                () -> reservationService.updateReservation(
                        1L,
                        request,
                        1L,
                        false
                )
        );

        verify(reservationRepo, never())
                .save(any());
    }


    // =========================================================
    // CONFIRM RESERVATION
    // =========================================================

    @Test
    void confirmReservation_shouldConfirmSuccessfully() {

        User user = User.builder()
                .id(1L)
                .build();

        Resource resource = Resource.builder()
                .id(1L)
                .name("Conference Room")
                .build();

        Reservation reservation = Reservation.builder()
                .id(1L)
                .user(user)
                .resource(resource)
                .status(ReservationStatus.PENDING)
                .build();

        when(reservationRepo.findById(1L))
                .thenReturn(Optional.of(reservation));

        when(reservationRepo.save(reservation))
                .thenReturn(reservation);

        ReservationResponse response =
                reservationService.confirmReservation(1L);

        assertNotNull(response);

        assertEquals(
                ReservationStatus.CONFIRMED,
                reservation.getStatus()
        );

        assertNotNull(
                reservation.getApprovedAt()
        );

        verify(reservationRepo)
                .save(reservation);
    }


    @Test
    void confirmReservation_shouldThrowException_whenCancelled() {

        Reservation reservation = Reservation.builder()
                .id(1L)
                .status(ReservationStatus.CANCELLED)
                .build();

        when(reservationRepo.findById(1L))
                .thenReturn(Optional.of(reservation));

        assertThrows(
                ReservationCannotBeConfirmedException.class,
                () -> reservationService.confirmReservation(1L)
        );

        verify(reservationRepo, never())
                .save(any());
    }
}