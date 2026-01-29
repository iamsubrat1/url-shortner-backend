package com.urlshortener.urlshortenerbackend.dto;

import java.time.LocalDateTime;

public record ShortenUrlResponse(String shortCode,
                                 LocalDateTime expiresAt,
                                 LocalDateTime createdAt) {

}


