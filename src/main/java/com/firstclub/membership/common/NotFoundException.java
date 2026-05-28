package com.firstclub.membership.common;

/** Thrown when a requested resource does not exist. Mapped to HTTP 404. */
public class NotFoundException extends RuntimeException {

    public NotFoundException(String message) {
        super(message);
    }

    public static NotFoundException of(String type, Object id) {
        return new NotFoundException(type + " not found: " + id);
    }
}
