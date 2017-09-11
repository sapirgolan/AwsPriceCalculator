package com.sap.ucp.rules;

import org.junit.rules.ExternalResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

@Component
public class CacheInvalidation extends ExternalResource {

    @Autowired
    private CacheManager cacheManager;

    @Override
    protected void before() {
        cacheManager.getCacheNames().stream().   // gets the name of all caches as a stream
                map(cacheManager::getCache).     // map the cache names to a stream of Cache:s
                forEach(Cache::clear);           // and clear() them
    }
}
