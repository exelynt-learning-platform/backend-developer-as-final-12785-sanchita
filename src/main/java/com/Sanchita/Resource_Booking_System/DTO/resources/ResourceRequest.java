package com.Sanchita.Resource_Booking_System.DTO.resources;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResourceRequest {

    @NotBlank(message = "Resource name is required")
    @Size(max = 150, message = "Resource name must not exceed 150 characters")
    private String name;

    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;

    @Size(max = 500, message = "Location must not exceed 500 characters")
    private String location;

    @NotNull(message = "Resource type ID is required")
    private Long resourceTypeId;

    @NotNull(message = "Price per hour is required")
    @DecimalMin(
            value = "0.01",
            message = "Price per hour must be greater than 0"
    )
    private BigDecimal pricePerHour;

    private Boolean active;

}
