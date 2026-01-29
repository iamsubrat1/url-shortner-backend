package com.urlshortener.urlshortenerbackend.dto;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDateTime;

public record ShortenUrlRequest(@NotBlank(message = "Long URL must not be blank")
                                @URL
                                String longUrl, String customCode, LocalDateTime expiresAt) {

}
