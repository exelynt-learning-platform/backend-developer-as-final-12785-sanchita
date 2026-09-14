package com.Sanchita.Resource_Booking_System.DTO.resources;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ResourceResponse {

    private Long id;
    private String name;
    private String description;
    private String location;
    private Long resourceTypeId;
    private String resourceTypeName;
    private BigDecimal pricePerHour;
    private boolean active;

}
