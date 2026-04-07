package com.controlfinance.common.utils;

import java.time.*;

public final class DateUtils {
  private DateUtils() {}

  public static Instant nowUtc() { return Instant.now(); }

  public static YearMonth ym(Instant instant, ZoneId zone) {
    return YearMonth.from(LocalDateTime.ofInstant(instant, zone));
  }
}
