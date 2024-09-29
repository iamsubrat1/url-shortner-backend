package com.urlshortener.urlshortenerbackend.repository;

import com.urlshortener.urlshortenerbackend.model.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UrlRepository extends JpaRepository<Url, Long> {
    Url findByShortUrl(String shortUrl);

    void deleteByShortUrl(String shortUrl);

    Optional<Url> findByLongUrl(String longUrl);
}
