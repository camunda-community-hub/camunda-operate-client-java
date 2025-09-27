package io.camunda.operate.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.camunda.client.api.search.filter.builder.DateTimeProperty;
import io.camunda.operate.serializer.OperateDateDeserializer;
import io.camunda.operate.serializer.OperateDateSerializer;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.function.Consumer;

@JsonSerialize(using = OperateDateSerializer.class)
@JsonDeserialize(using = OperateDateDeserializer.class)
public sealed interface OperateDate {
  static OperateDate date(Date date) {
    return new SimpleOperateDate(date);
  }

  static OperateDate date(OffsetDateTime date) {
    return new OffsetOperateDate(date);
  }

  static OperateDate filter(Date date, DateFilterRange range) {
    return new OperateDateFilter(date, range);
  }

  static OperateDate empty() {
    return new SimpleOperateDate(null);
  }

  Date getDate();

  OffsetDateTime getOffsetDateTime();

  Consumer<DateTimeProperty> applyTo();

  record SimpleOperateDate(Date date) implements OperateDate {

    @Override
    public Date getDate() {
      return date;
    }

    @Override
    public OffsetDateTime getOffsetDateTime() {
      return date.toInstant().atOffset(ZoneOffset.UTC);
    }

    @Override
    public Consumer<DateTimeProperty> applyTo() {
      return dateTimeProperty -> dateTimeProperty.eq(getOffsetDateTime());
    }
  }

  record OffsetOperateDate(OffsetDateTime date) implements OperateDate {
    @Override
    public Date getDate() {
      return Date.from(date.toInstant());
    }

    @Override
    public OffsetDateTime getOffsetDateTime() {
      return date;
    }

    @Override
    public Consumer<DateTimeProperty> applyTo() {
      return dateTimeProperty -> dateTimeProperty.eq(date);
    }
  }

  record OperateDateFilter(Date date, DateFilterRange range) implements OperateDate {
    @Override
    public Date getDate() {
      return date;
    }

    @Override
    public OffsetDateTime getOffsetDateTime() {
      return date.toInstant().atOffset(ZoneOffset.UTC);
    }

    @Override
    public Consumer<DateTimeProperty> applyTo() {
      return dateTimeProperty ->
          dateTimeProperty.gte(range.lowerBoundary(date)).lte(range.higherBoundary(date));
    }
  }

  record OperateOffsetDateTimeFilter(OffsetDateTime date, DateFilterRange range)
      implements OperateDate {
    @Override
    public Date getDate() {
      return Date.from(date.toInstant());
    }

    @Override
    public OffsetDateTime getOffsetDateTime() {
      return date.toInstant().atOffset(ZoneOffset.UTC);
    }

    @Override
    public Consumer<DateTimeProperty> applyTo() {
      return dateTimeProperty ->
          dateTimeProperty.gte(range.lowerBoundary(date)).lte(range.higherBoundary(date));
    }
  }
}
