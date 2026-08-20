package com.major.CampuSphere.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Base application exception. Carries HTTP status and error code.
 */
@Getter
public class CampuSphereException extends RuntimeException {

    private final HttpStatus status;
    private final String errorCode;

    public CampuSphereException(String message, HttpStatus status, String errorCode) {
        super(message);
        this.status = status;
        this.errorCode = errorCode;
    }
}
