package com.Sanchita.Resource_Booking_System.DTO.reservation;

import com.Sanchita.Resource_Booking_System.Enums.ReservationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReservationResponse {


    private Long id;

    private String reservationNumber;

    private Long resourceId;

    private String resourceName;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private BigDecimal price;

    private ReservationStatus status;

    private String notes;

    private String cancellationReason;

    private LocalDateTime cancelledAt;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
