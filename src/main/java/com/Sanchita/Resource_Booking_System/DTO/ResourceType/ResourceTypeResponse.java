package com.Sanchita.Resource_Booking_System.DTO.ResourceType;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class ResourceTypeResponse {

    private Long id;
    private String name;
    private String description;
    private boolean active;

}
