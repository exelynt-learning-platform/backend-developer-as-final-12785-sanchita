package com.Sanchita.Resource_Booking_System.IntegrationTesting;

import com.Sanchita.Resource_Booking_System.Entity.Reservation;
import com.Sanchita.Resource_Booking_System.Entity.Resource;
import com.Sanchita.Resource_Booking_System.Entity.ResourceType;
import com.Sanchita.Resource_Booking_System.Entity.User;
import com.Sanchita.Resource_Booking_System.Enums.ReservationStatus;
import com.Sanchita.Resource_Booking_System.Enums.Role;
import com.Sanchita.Resource_Booking_System.Repository.ReservationRepo;
import com.Sanchita.Resource_Booking_System.Repository.ResourceRepo;
import com.Sanchita.Resource_Booking_System.Repository.ResourceTypeRepo;
import com.Sanchita.Resource_Booking_System.Repository.UserRepo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ReservationIntegrationTest {

    @Autowired
    private ReservationRepo reservationRepo;

    @Autowired
    private ResourceRepo resourceRepo;

    @Autowired
    private ResourceTypeRepo resourceTypeRepo;

    @Autowired
    private UserRepo userRepo;

    private User user;
    private Resource resource;

    @BeforeEach
    void setUp() {

        ResourceType resourceType = ResourceType.builder()
                .name("Vehicle")
                .description("Vehicle resources")
                .active(true)
                .build();

        resourceType = resourceTypeRepo.save(resourceType);

        user = User.builder()
                .name("Test User")
                .email("testuser@gmail.com")
                .password("password123")
                .role(Role.USER)
                .build();

        user = userRepo.save(user);

        resource = Resource.builder()
                .name("Conference Room")
                .description("Test conference room")
                .location("Pune Office")
                .resourceType(resourceType)
                .pricePerHour(new BigDecimal("500.00"))
                .active(true)
                .build();

        resource = resourceRepo.save(resource);
    }

    @Test
    void findByUserId_shouldReturnUserReservations() {

        Reservation reservation = createReservation(
                LocalDateTime.of(2026, 9, 15, 10, 0),
                LocalDateTime.of(2026, 9, 15, 12, 0)
        );

        reservationRepo.save(reservation);

        List<Reservation> result =
                reservationRepo.findByUserId(user.getId());

        assertThat(result).hasSize(1);

        assertThat(result.get(0).getUser().getId())
                .isEqualTo(user.getId());
    }

    @Test
    void existsOverlappingReservation_shouldReturnTrue_whenTimeOverlaps() {

        Reservation reservation = createReservation(
                LocalDateTime.of(2026, 9, 15, 10, 0),
                LocalDateTime.of(2026, 9, 15, 12, 0)
        );

        reservationRepo.save(reservation);

        boolean result =
                reservationRepo.existsOverlappingReservation(
                        resource.getId(),
                        LocalDateTime.of(2026, 9, 15, 11, 0),
                        LocalDateTime.of(2026, 9, 15, 13, 0),
                        List.of(
                                ReservationStatus.PENDING,
                                ReservationStatus.CONFIRMED
                        )
                );

        assertThat(result).isTrue();
    }

    @Test
    void existsOverlappingReservation_shouldReturnFalse_whenNoOverlap() {

        Reservation reservation = createReservation(
                LocalDateTime.of(2026, 9, 15, 10, 0),
                LocalDateTime.of(2026, 9, 15, 12, 0)
        );

        reservationRepo.save(reservation);

        boolean result =
                reservationRepo.existsOverlappingReservation(
                        resource.getId(),
                        LocalDateTime.of(2026, 9, 15, 12, 0),
                        LocalDateTime.of(2026, 9, 15, 14, 0),
                        List.of(
                                ReservationStatus.PENDING,
                                ReservationStatus.CONFIRMED
                        )
                );

        assertThat(result).isFalse();
    }

    @Test
    void existsOverlappingReservation_shouldReturnFalse_whenReservationIsCancelled() {

        Reservation reservation = createReservation(
                LocalDateTime.of(2026, 9, 15, 10, 0),
                LocalDateTime.of(2026, 9, 15, 12, 0)
        );

        reservation.setStatus(ReservationStatus.CANCELLED);

        reservationRepo.save(reservation);

        boolean result =
                reservationRepo.existsOverlappingReservation(
                        resource.getId(),
                        LocalDateTime.of(2026, 9, 15, 11, 0),
                        LocalDateTime.of(2026, 9, 15, 13, 0),
                        List.of(
                                ReservationStatus.PENDING,
                                ReservationStatus.CONFIRMED
                        )
                );

        assertThat(result).isFalse();
    }

    @Test
    void existsOverlappingReservationForUpdate_shouldIgnoreCurrentReservation() {

        Reservation reservation = createReservation(
                LocalDateTime.of(2026, 9, 15, 10, 0),
                LocalDateTime.of(2026, 9, 15, 12, 0)
        );

        reservation = reservationRepo.save(reservation);

        boolean result =
                reservationRepo.existsOverlappingReservationForUpdate(
                        reservation.getId(),
                        resource.getId(),
                        LocalDateTime.of(2026, 9, 15, 10, 0),
                        LocalDateTime.of(2026, 9, 15, 12, 0),
                        List.of(
                                ReservationStatus.PENDING,
                                ReservationStatus.CONFIRMED
                        )
                );

        assertThat(result).isFalse();
    }

    @Test
    void findReservations_shouldFilterByStatus() {

        Reservation pending = createReservation(
                LocalDateTime.of(2026, 9, 15, 10, 0),
                LocalDateTime.of(2026, 9, 15, 11, 0)
        );

        pending.setStatus(ReservationStatus.PENDING);

        Reservation confirmed = createReservation(
                LocalDateTime.of(2026, 9, 16, 10, 0),
                LocalDateTime.of(2026, 9, 16, 11, 0)
        );

        confirmed.setStatus(ReservationStatus.CONFIRMED);

        reservationRepo.save(pending);
        reservationRepo.save(confirmed);

        Page<Reservation> result =
                reservationRepo.findReservations(
                        ReservationStatus.PENDING,
                        null,
                        null,
                        PageRequest.of(0, 10)
                );

        assertThat(result.getContent()).hasSize(1);

        assertThat(result.getContent().get(0).getStatus())
                .isEqualTo(ReservationStatus.PENDING);
    }

    @Test
    void findReservations_shouldFilterByMinAndMaxPrice() {

        Reservation reservation1 = createReservation(
                LocalDateTime.of(2026, 9, 15, 10, 0),
                LocalDateTime.of(2026, 9, 15, 11, 0)
        );

        reservation1.setPrice(new BigDecimal("500.00"));

        Reservation reservation2 = createReservation(
                LocalDateTime.of(2026, 9, 16, 10, 0),
                LocalDateTime.of(2026, 9, 16, 12, 0)
        );

        reservation2.setPrice(new BigDecimal("1000.00"));

        reservationRepo.save(reservation1);
        reservationRepo.save(reservation2);

        Page<Reservation> result =
                reservationRepo.findReservations(
                        null,
                        new BigDecimal("600.00"),
                        new BigDecimal("1200.00"),
                        PageRequest.of(0, 10)
                );

        assertThat(result.getContent()).hasSize(1);

        assertThat(result.getContent().get(0).getPrice())
                .isEqualByComparingTo("1000.00");
    }

    private Reservation createReservation(
            LocalDateTime startTime,
            LocalDateTime endTime) {

        return Reservation.builder()
                .reservationNumber("RES-" + System.nanoTime())
                .resource(resource)
                .user(user)
                .startTime(startTime)
                .endTime(endTime)
                .price(new BigDecimal("500.00"))
                .status(ReservationStatus.PENDING)
                .notes("Test reservation")
                .build();
    }
}