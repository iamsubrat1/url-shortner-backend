package com.urlshortener.urlshortenerbackend.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "urls",
        indexes = {
                @Index(
                        name = "idx_short_url",
                        columnList = "short_url",
                        unique = true
                ),
                @Index(
                        name = "idx_long_url",
                        columnList = "long_url"
                )

        }

)
public class Url {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "long_url", nullable = false, length = 2048)
    private String longUrl;

    @Column(name = "short_url", nullable = false, length = 32, unique = true)
    private String shortUrl;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;
}
