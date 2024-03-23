package com.kustlik.medicalclinic.exception;

public class InvalidDateTimeException extends RuntimeException{
    public InvalidDateTimeException(String message) {
        super(message);
    }
}
