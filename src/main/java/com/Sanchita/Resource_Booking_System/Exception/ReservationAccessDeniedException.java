package com.Sanchita.Resource_Booking_System.Exception;

public class ReservationAccessDeniedException extends RuntimeException{

    public ReservationAccessDeniedException(String message)
    {
        super(message);
    }
}
