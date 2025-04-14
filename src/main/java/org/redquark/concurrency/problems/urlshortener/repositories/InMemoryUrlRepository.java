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
    public void save(UrlMapping urlMapping) {
        this.longToShortMappings.putIfAbsent(urlMapping.getLongUrl(), urlMapping.getShortCode());
        this.shortToLongMappings.putIfAbsent(urlMapping.getShortCode(), urlMapping.getLongUrl());
    }
}
