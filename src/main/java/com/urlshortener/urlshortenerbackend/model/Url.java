package com.urlshortener.urlshortenerbackend.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "urls")
public class Url {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String longUrl;

    @Column(nullable = false, unique = true)
    private String shortUrl;

    @Column(name = "created_at", columnDefinition = "DATETIME")
    private LocalDateTime createdAt;
}
