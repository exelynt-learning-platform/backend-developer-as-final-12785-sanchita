package com.Sanchita.Resource_Booking_System.Repository;

import com.Sanchita.Resource_Booking_System.Entity.Reservation;
import com.Sanchita.Resource_Booking_System.Enums.ReservationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


@Repository
public interface ReservationRepo extends JpaRepository<Reservation, Long> {

    List<Reservation> findByUserId(Long userId);

    @Query("""
            SELECT COUNT(r) > 0
            FROM Reservation r
            WHERE r.resource.id = :resourceId
            AND r.status IN :statuses
            AND r.startTime < :endTime
            AND r.endTime > :startTime
            """)
    boolean existsOverlappingReservation(
            @Param("resourceId") Long resourceId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            @Param("statuses") List<ReservationStatus> statuses
    );


    @Query("""
        SELECT COUNT(r) > 0
        FROM Reservation r
        WHERE r.resource.id = :resourceId
        AND r.id <> :reservationId
        AND r.status IN :statuses
        AND r.startTime < :endTime
        AND r.endTime > :startTime
        """)
    boolean existsOverlappingReservationForUpdate(
            @Param("reservationId") Long reservationId,
            @Param("resourceId") Long resourceId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            @Param("statuses") List<ReservationStatus> statuses
    );

    @Query("""
            SELECT r
            FROM Reservation r
            WHERE (:status IS NULL OR r.status = :status)
            AND (:minPrice IS NULL OR r.price >= :minPrice)
            AND (:maxPrice IS NULL OR r.price <= :maxPrice)
            """)
    Page<Reservation> findReservations(
            @Param("status") ReservationStatus status,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            Pageable pageable
    );
}
