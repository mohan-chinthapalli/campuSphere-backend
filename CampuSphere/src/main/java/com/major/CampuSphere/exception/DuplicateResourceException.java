package com.major.CampuSphere.exception;

import org.springframework.http.HttpStatus;

public class DuplicateResourceException extends CampuSphereException {

    public DuplicateResourceException(String message) {
        super(message, HttpStatus.CONFLICT, "DUPLICATE_RESOURCE");
    }
}
