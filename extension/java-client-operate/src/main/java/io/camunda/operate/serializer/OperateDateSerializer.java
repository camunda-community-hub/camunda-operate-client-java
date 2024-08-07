package io.camunda.operate.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import io.camunda.operate.model.OperateDate;
import io.camunda.operate.model.OperateDate.OperateDateFilter;
import io.camunda.operate.model.OperateDate.SimpleOperateDate;
import java.io.IOException;
import java.text.SimpleDateFormat;

public class OperateDateSerializer extends JsonSerializer<OperateDate> {
  public static final SimpleDateFormat isoFormat =
      new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZZ");

  public static String filterSeparator = "||/";

  @Override
  public void serialize(OperateDate value, JsonGenerator gen, SerializerProvider serializers)
      throws IOException {
    if (value.getDate() == null) {
      gen.writeNull();
    } else if (value instanceof SimpleOperateDate simple) {
      if (simple.date() != null) {
        gen.writeString(isoFormat.format(simple.date()));
      }
    } else if (value instanceof OperateDateFilter filter) {
      gen.writeString(isoFormat.format(filter.date()) + filterSeparator + filter.range().value());
    }
  }
}
