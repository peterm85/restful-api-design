package org.example.restful.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@ConfigurationProperties(prefix = "caching")
public class CacheTTL {

  private Integer allStockTTL; // Milliseconds
}
