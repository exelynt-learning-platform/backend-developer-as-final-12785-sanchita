package com.Sanchita.Resource_Booking_System.Exception;

public class ReservationCannotBeConfirmedException extends RuntimeException{

    public ReservationCannotBeConfirmedException(String message)
    {
        super(message);
    }
}
