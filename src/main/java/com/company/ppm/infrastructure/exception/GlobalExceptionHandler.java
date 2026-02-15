package com.company.ppm.infrastructure.exception;

import com.company.ppm.domain.exception.AuthenticationFailedException;
import com.company.ppm.domain.exception.ConflictException;
import com.company.ppm.domain.exception.DomainException;
import com.company.ppm.domain.exception.ForbiddenOperationException;
import com.company.ppm.domain.exception.ResourceNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problem.setTitle("Validation failed");
        List<Map<String, String>> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::toErrorMap)
                .toList();
        problem.setProperty("errors", errors);
        return problem;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ProblemDetail handleConstraintViolation(ConstraintViolationException ex) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problem.setTitle("Validation failed");
        problem.setDetail(ex.getMessage());
        return problem;
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ProblemDetail handleMessageNotReadable(HttpMessageNotReadableException ex) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problem.setTitle("Malformed request body");
        return problem;
    }

    @ExceptionHandler(AuthenticationFailedException.class)
    public ProblemDetail handleAuthFailed(AuthenticationFailedException ex) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED);
        problem.setTitle("Authentication failed");
        problem.setDetail(ex.getMessage());
        return problem;
    }

    @ExceptionHandler({ForbiddenOperationException.class, AccessDeniedException.class})
    public ProblemDetail handleForbidden(Exception ex) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.FORBIDDEN);
        problem.setTitle("Access denied");
        problem.setDetail(ex.getMessage());
        return problem;
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ProblemDetail handleNotFound(ResourceNotFoundException ex) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        problem.setTitle("Resource not found");
        problem.setDetail(ex.getMessage());
        return problem;
    }

    @ExceptionHandler({ConflictException.class, OptimisticLockingFailureException.class})
    public ProblemDetail handleConflict(Exception ex) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        problem.setTitle("Conflict");
        problem.setDetail(ex.getMessage());
        return problem;
    }

    @ExceptionHandler(AuthenticationException.class)
    public ProblemDetail handleSecurityAuthentication(AuthenticationException ex) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED);
        problem.setTitle("Unauthorized");
        problem.setDetail("Authentication is required");
        return problem;
    }

    @ExceptionHandler(DomainException.class)
    public ProblemDetail handleDomain(DomainException ex) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problem.setTitle("Domain validation failed");
        problem.setDetail(ex.getMessage());
        return problem;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleUnexpected(Exception ex) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        problem.setTitle("Unexpected server error");
        return problem;
    }

    private Map<String, String> toErrorMap(FieldError fieldError) {
        return Map.of(
                "field", fieldError.getField(),
                "message", fieldError.getDefaultMessage() == null ? "invalid value" : fieldError.getDefaultMessage()
        );
    }
}
