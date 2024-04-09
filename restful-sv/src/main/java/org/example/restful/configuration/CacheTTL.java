package org.example.restful.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@ConfigurationProperties(prefix = CacheTTL.CACHING_TAG)
public class CacheTTL {

  public static final String CACHING_TAG = "caching";
  public static final String ALL_STOCKS_TLL_STRING = "${" + CacheTTL.CACHING_TAG + ".allStocksTTL}";

  private Integer allStocksTTL; // Milliseconds
}
