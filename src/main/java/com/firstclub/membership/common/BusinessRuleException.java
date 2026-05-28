package com.firstclub.membership.common;

/**
 * Thrown when a request is well-formed but violates a domain rule or current
 * state (e.g. downgrading below the base tier, paying an already-settled fee).
 * Mapped to HTTP 409 Conflict.
 */
public class BusinessRuleException extends RuntimeException {

    public BusinessRuleException(String message) {
        super(message);
    }
}
