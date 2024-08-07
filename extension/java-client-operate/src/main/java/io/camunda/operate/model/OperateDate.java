package io.camunda.operate.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.camunda.operate.serializer.OperateDateDeserializer;
import io.camunda.operate.serializer.OperateDateSerializer;
import java.util.Date;

@JsonSerialize(using = OperateDateSerializer.class)
@JsonDeserialize(using = OperateDateDeserializer.class)
public sealed interface OperateDate {
  static OperateDate date(Date date) {
    return new SimpleOperateDate(date);
  }

  static OperateDate filter(Date date, DateFilterRange range) {
    return new OperateDateFilter(date, range);
  }

  static OperateDate empty() {
    return new SimpleOperateDate(null);
  }

  Date getDate();

  record SimpleOperateDate(Date date) implements OperateDate {

    @Override
    public Date getDate() {
      return date;
    }
  }

  record OperateDateFilter(Date date, DateFilterRange range) implements OperateDate {
    @Override
    public Date getDate() {
      return date;
    }
  }
}
