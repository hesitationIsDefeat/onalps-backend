package dev.onat.onalps.exceptions;

public class InvalidUUIDStringException extends RuntimeException {
    public InvalidUUIDStringException() {
        super("Invalid UUID string");
    }
}
