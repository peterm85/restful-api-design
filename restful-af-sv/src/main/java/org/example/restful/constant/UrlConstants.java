package org.example.restful.constant;

import lombok.experimental.UtilityClass;

@UtilityClass
public class UrlConstants {

  public static final String BASE_PATH = "/api/invest";
  public static final String BASE_PATH_V1 = BASE_PATH + "/v1";
  public static final String BASE_PATH_V2 = BASE_PATH + "/v2";
  public static final String SLASH = "/";

  public static final String INVESTORS_SUBPATH = SLASH + "investors";
  public static final String ID_PATH_PARAM = SLASH + "{id}";

  public static final String STOCKS_SUBPATH = SLASH + "stocks";
  public static final String ISIN_PATH_PARAM = SLASH + "{isin}";

  public static final String PURCHASE_ACTION = SLASH + "purchase";
}
