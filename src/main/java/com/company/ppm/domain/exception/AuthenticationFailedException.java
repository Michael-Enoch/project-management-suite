package com.company.ppm.domain.exception;

public class AuthenticationFailedException extends DomainException {
    public AuthenticationFailedException(String message) {
        super(message);
    }
}
