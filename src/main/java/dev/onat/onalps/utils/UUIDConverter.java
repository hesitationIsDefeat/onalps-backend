package dev.onat.onalps.utils;

import dev.onat.onalps.exceptions.InvalidUUIDStringException;

import java.util.UUID;

public class UUIDConverter {
    public static UUID convertToUUID(String uuidString) throws InvalidUUIDStringException {
        try {
            return UUID.fromString(uuidString);
        } catch (IllegalArgumentException e) {
            throw new InvalidUUIDStringException();
        }
    }
}
