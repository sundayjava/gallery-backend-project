package com.gallery.gallery.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class AlreadyExistException extends RuntimeException{
    private final String fieldName;

    public AlreadyExistException(String fieldName) {
        super(String.format("%s already exist", fieldName));
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }
}
