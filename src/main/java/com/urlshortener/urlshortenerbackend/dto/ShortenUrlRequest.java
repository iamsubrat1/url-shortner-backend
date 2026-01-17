package com.urlshortener.urlshortenerbackend.dto;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;

public record ShortenUrlRequest(@NotBlank
                                @URL
                                String longUrl) {

}
