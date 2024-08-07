package io.camunda.operate.serializer;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import io.camunda.operate.model.DateFilterRange;
import io.camunda.operate.model.OperateDate;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

public class OperateDateDeserializer extends JsonDeserializer<OperateDate> {
  @Override
  public OperateDate deserialize(JsonParser p, DeserializationContext ctxt)
      throws IOException, JacksonException {
    String serializedDate = p.readValueAs(String.class);
    if (isFilterExpression(serializedDate)) {
      return parseFilterExpression(serializedDate);
    } else {
      return OperateDate.date(parseDate(serializedDate));
    }
  }

  private OperateDate parseFilterExpression(String serializedDate) {
    String[] split = serializedDate.split("\\|\\|\\/");
    if (split.length == 2) {
      String date = split[0];
      String filterRange = split[1];
      return OperateDate.filter(parseDate(date), DateFilterRange.from(filterRange));
    }
    throw new IllegalStateException("Not a filter expression: " + serializedDate);
  }

  private Date parseDate(String serializedDate) {
    try {
      return OperateDateSerializer.isoFormat.parse(serializedDate);
    } catch (ParseException e) {
      throw new RuntimeException("Could not parse date from string '" + serializedDate + "'", e);
    }
  }

  private boolean isFilterExpression(String serializedDate) {
    return serializedDate.contains(OperateDateSerializer.filterSeparator);
  }
}
