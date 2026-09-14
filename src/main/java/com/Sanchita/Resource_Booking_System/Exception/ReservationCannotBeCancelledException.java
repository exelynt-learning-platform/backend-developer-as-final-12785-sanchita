package com.Sanchita.Resource_Booking_System.Exception;

public class ReservationCannotBeCancelledException extends RuntimeException{

    public ReservationCannotBeCancelledException(String message)
    {
        super(message);
    }
}
