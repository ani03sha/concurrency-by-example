package org.redquark.concurrency.problems.urlshortener.repositories;

import org.redquark.concurrency.problems.urlshortener.domains.UrlMapping;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class InMemoryUrlRepository implements UrlRepository {

    private final ConcurrentMap<String, String> longToShortMappings = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, String> shortToLongMappings = new ConcurrentHashMap<>();

    @Override
    public Optional<String> findShortcode(String longUrl) {
        return Optional.ofNullable(this.longToShortMappings.get(longUrl));
    }

    @Override
    public Optional<String> findLongUrl(String shortCode) {
        return Optional.ofNullable(this.shortToLongMappings.get(shortCode));
    }

    @Override
    public boolean saveIfAbsent(UrlMapping urlMapping) {
        // Try to put the longUrl -> shortCode atomically
        final String existing = this.longToShortMappings.putIfAbsent(urlMapping.getLongUrl(), urlMapping.getShortCode());
        if (existing == null) {
            // Successfully inserted longUrl -> shortCode mapping
            this.shortToLongMappings.put(urlMapping.getShortCode(), urlMapping.getLongUrl());
            return true;
        }
        return false;
    }
}
