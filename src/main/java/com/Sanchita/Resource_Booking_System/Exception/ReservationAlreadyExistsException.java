package com.Sanchita.Resource_Booking_System.Exception;

public class ReservationAlreadyExistsException extends RuntimeException{

    public ReservationAlreadyExistsException(String message)
    {
        super(message);
    }
}
