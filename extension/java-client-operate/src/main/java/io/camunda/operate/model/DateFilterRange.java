package io.camunda.operate.model;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Date;

public enum DateFilterRange {
  YEAR("y", ChronoUnit.YEARS),
  MONTH("M", ChronoUnit.MONTHS),
  WEEK("w", ChronoUnit.WEEKS),
  DAY("d", ChronoUnit.DAYS),
  HOUR("h", ChronoUnit.HOURS),
  MINUTE("m", ChronoUnit.MINUTES),
  SECOND("s", ChronoUnit.SECONDS);

  private final String value;
  private final TemporalUnit temporalUnit;

  DateFilterRange(String value, TemporalUnit temporalUnit) {
    this.value = value;
    this.temporalUnit = temporalUnit;
  }

  public static DateFilterRange from(String filterRange) {
    for (DateFilterRange filter : DateFilterRange.values()) {
      if (filter.value.equals(filterRange)) {
        return filter;
      }
    }
    throw new IllegalArgumentException("Invalid filter range: " + filterRange);
  }

  public String value() {
    return value;
  }

  public OffsetDateTime lowerBoundary(Date date) {
    return lowerBoundary(date.toInstant().atOffset(ZoneOffset.UTC));
  }

  public OffsetDateTime higherBoundary(Date date) {
    return higherBoundary(date.toInstant().atOffset(ZoneOffset.UTC));
  }

  public OffsetDateTime lowerBoundary(OffsetDateTime date) {
    return date.truncatedTo(temporalUnit);
  }

  public OffsetDateTime higherBoundary(OffsetDateTime date) {
    return date.truncatedTo(temporalUnit).plus(1, temporalUnit);
  }
}
