package io.camunda.operate.model;

public enum DateFilterRange {
  YEAR("y"),
  MONTH("M"),
  WEEK("w"),
  DAY("d"),
  HOUR("h"),
  MINUTE("m"),
  SECOND("s");

  private final String value;

  DateFilterRange(String value) {
    this.value = value;
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
}
