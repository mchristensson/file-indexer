package org.se.mac.blorksandbox.controller;

import java.util.Arrays;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for the application's caching mechanism.
 */
@Configuration
@EnableCaching
public class CachingConfig {

    /**
     * Provides a CacheManager instance.
     *
     * @return Configured CacheManager instance
     */
    @Bean
    public CacheManager cacheManager() {
        SimpleCacheManager cacheManager = new SimpleCacheManager();
        cacheManager.setCaches(Arrays.asList(new ConcurrentMapCache("jobqueueimpl"),
                new ConcurrentMapCache("addresses")));
        return cacheManager;
    }
}
