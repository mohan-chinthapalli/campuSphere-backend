package com.major.CampuSphere.exception;

import org.springframework.http.HttpStatus;

public class ForbiddenException extends CampuSphereException {

    public ForbiddenException(String message) {
        super(message, HttpStatus.FORBIDDEN, "FORBIDDEN");
    }
}
