package com.urlshortener.urlshortenerbackend.repository;

import com.urlshortener.urlshortenerbackend.model.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UrlMappingRepository extends JpaRepository<Url, Long> {

    /**
     * Fetch URL mapping by short code.
     */
    Optional<Url> findByShortUrl(String shortUrl);

    /**
     * Fetch URL mapping by long URL (used for idempotency).
     */
    Optional<Url> findByLongUrl(String longUrl);

    /**
     * Checks existence of short code (used for collision detection).
     */
    boolean existsByShortUrl(String shortUrl);

    /**
     * Deletes URL mapping by short code.
     */
    void deleteByShortUrl(String shortUrl);
}
