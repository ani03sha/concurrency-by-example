package org.redquark.concurrency.problems.urlshortener;

import org.redquark.concurrency.problems.urlshortener.domains.UrlMapping;
import org.redquark.concurrency.problems.urlshortener.encoders.Encoder;
import org.redquark.concurrency.problems.urlshortener.repositories.UrlRepository;

import java.util.concurrent.atomic.AtomicLong;

public class UrlShortenerService {

    private final UrlRepository repository;
    private final Encoder encoder;
    private final AtomicLong counter;


    public UrlShortenerService(UrlRepository repository, Encoder encoder) {
        this.repository = repository;
        this.encoder = encoder;
        this.counter = new AtomicLong(100000); // Start at a non-trivial value
    }

    public String shortenUrl(String longUrl) {
        return repository.findShortcode(longUrl)
                .orElseGet(() -> {
                    final String shortCode = encoder.encode(counter.getAndIncrement());
                    repository.save(new UrlMapping(longUrl, shortCode));
                    return shortCode;
                });
    }

    public String getOriginalUrl(String shortCode) {
        return repository.findLongUrl(shortCode)
                .orElseThrow(() -> new IllegalArgumentException("Short code: " + shortCode + " not found"));
    }
}
