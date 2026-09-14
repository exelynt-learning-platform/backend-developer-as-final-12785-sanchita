package com.Sanchita.Resource_Booking_System.Exception;

import org.springframework.data.jpa.repository.JpaRepository;

public class ResourceNotFoundException  extends RuntimeException {

    public ResourceNotFoundException(String message)
    {
        super(message);
    }
}
