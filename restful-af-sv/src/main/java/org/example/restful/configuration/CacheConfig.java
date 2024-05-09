package org.example.restful.configuration;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableCaching
@EnableScheduling
@Slf4j
public class CacheConfig {

  @CacheEvict(value = "allstocks", allEntries = true)
  @Scheduled(fixedRateString = CacheTTL.ALL_STOCKS_TLL_STRING) // Milliseconds
  public void emptyCache() {
    log.info("Emptying cache");
  }
}
