package com.company.ppm.domain.exception;

public class ForbiddenOperationException extends DomainException {
    public ForbiddenOperationException(String message) {
        super(message);
    }
}
