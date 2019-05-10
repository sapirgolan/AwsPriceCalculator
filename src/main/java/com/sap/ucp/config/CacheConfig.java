package com.sap.ucp.config;

import org.springframework.boot.autoconfigure.cache.CacheManagerCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CacheConfig {

  @Bean
  public CacheManagerCustomizer<CaffeineCacheManager> cacheManagerCustomizer() {
    return cacheManager -> cacheManager.setAllowNullValues(false);
  }
}
