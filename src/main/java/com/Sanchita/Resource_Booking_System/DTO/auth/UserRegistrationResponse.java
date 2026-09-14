package com.Sanchita.Resource_Booking_System.DTO.auth;

import com.Sanchita.Resource_Booking_System.Enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRegistrationResponse {

    private Long id;

    private String name;

    private String email;

    private Role role;
}
