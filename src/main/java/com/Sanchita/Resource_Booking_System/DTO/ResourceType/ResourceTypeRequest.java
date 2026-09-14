package com.Sanchita.Resource_Booking_System.DTO.ResourceType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class ResourceTypeRequest {

    @NotBlank(message = "Resource type name is required")
    @Size(
            max = 100,
            message = "Resource type name must not exceed 100 characters"
    )
    private String name;

    @Size(
            max = 500,
            message = "Description must not exceed 500 characters"
    )
    private String description;

}
