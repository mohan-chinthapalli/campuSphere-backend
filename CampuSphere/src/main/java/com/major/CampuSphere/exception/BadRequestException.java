package com.major.CampuSphere.exception;

import org.springframework.http.HttpStatus;

public class BadRequestException extends CampuSphereException {

    public BadRequestException(String message) {
        super(message, HttpStatus.BAD_REQUEST, "BAD_REQUEST");
    }
}
