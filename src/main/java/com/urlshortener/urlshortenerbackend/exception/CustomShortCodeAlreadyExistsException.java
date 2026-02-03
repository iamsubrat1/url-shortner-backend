package com.urlshortener.urlshortenerbackend.exception;

public class CustomShortCodeAlreadyExistsException extends RuntimeException {
    public CustomShortCodeAlreadyExistsException(String message) {
        super(message);
    }
}
