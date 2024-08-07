package io.camunda.operate.model;

import static org.assertj.core.api.Assertions.*;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.camunda.operate.serializer.OperateDateSerializer;
import java.util.Date;
import org.junit.jupiter.api.Test;

public class OperateDateTest {
  @Test
  void shouldSerializeDate() {
    Date startDate = new Date();
    String expectedStartDate = OperateDateSerializer.isoFormat.format(startDate);
    ObjectMapper objectMapper = new ObjectMapper();
    FlowNodeInstance fni = new FlowNodeInstance();
    fni.setStartDate(OperateDate.date(startDate));
    JsonNode jsonNode = objectMapper.valueToTree(fni);
    assertThat(jsonNode.has("startDate")).isTrue();
    assertThat(jsonNode.get("startDate").textValue()).isEqualTo(expectedStartDate);
  }

  @Test
  void shouldDeserializeDate() throws JsonProcessingException {
    Date startDate = new Date();
    String serializedStartDate = OperateDateSerializer.isoFormat.format(startDate);
    ObjectMapper objectMapper = new ObjectMapper();
    ObjectNode fni = JsonNodeFactory.instance.objectNode();
    fni.put("startDate", serializedStartDate);
    FlowNodeInstance deserialized = objectMapper.treeToValue(fni, FlowNodeInstance.class);
    assertThat(deserialized.getStartDate().getDate()).isEqualTo(startDate);
  }

  @Test
  void shouldSerializeNullDate() {
    ObjectMapper objectMapper = new ObjectMapper();
    FlowNodeInstance fni = new FlowNodeInstance();
    JsonNode jsonNode = objectMapper.valueToTree(fni);
    assertThat(jsonNode.has("startDate")).isTrue();
    assertThat(jsonNode.get("startDate").textValue()).isNull();
  }

  @Test
  void shouldSerializeNullDate_nonNullInclude() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.setSerializationInclusion(Include.NON_NULL);
    FlowNodeInstance fni = new FlowNodeInstance();
    JsonNode jsonNode = objectMapper.valueToTree(fni);
    assertThat(jsonNode.has("startDate")).isFalse();
  }

  @Test
  void shouldDeserializeNullDate() throws JsonProcessingException {
    ObjectMapper objectMapper = new ObjectMapper();
    ObjectNode fni = JsonNodeFactory.instance.objectNode();
    FlowNodeInstance deserialized = objectMapper.treeToValue(fni, FlowNodeInstance.class);
    assertThat(deserialized.getStartDate()).isNull();
  }

  @Test
  void shouldSerializeDateFilter() {
    Date startDate = new Date();
    ObjectMapper objectMapper = new ObjectMapper();
    FlowNodeInstance fni = new FlowNodeInstance();
    fni.setStartDate(OperateDate.filter(startDate, DateFilterRange.DAY));
    JsonNode jsonNode = objectMapper.valueToTree(fni);
    String serializedStartDate = OperateDateSerializer.isoFormat.format(startDate);
    assertThat(jsonNode.has("startDate")).isTrue();
    assertThat(jsonNode.get("startDate").textValue()).isEqualTo(serializedStartDate + "||/d");
  }

  @Test
  void shouldDeserializeDateFilter() throws JsonProcessingException {
    Date startDate = new Date();
    ObjectMapper objectMapper = new ObjectMapper();
    FlowNodeInstance fni = new FlowNodeInstance();
    OperateDate operateStartDate = OperateDate.filter(startDate, DateFilterRange.DAY);
    fni.setStartDate(operateStartDate);
    JsonNode jsonNode = objectMapper.valueToTree(fni);
    String serializedStartDate = OperateDateSerializer.isoFormat.format(startDate);
    assertThat(jsonNode.has("startDate")).isTrue();
    assertThat(jsonNode.get("startDate").textValue()).isEqualTo(serializedStartDate + "||/d");
    FlowNodeInstance fni2 = objectMapper.treeToValue(jsonNode, FlowNodeInstance.class);
    assertThat(fni2.getStartDate()).isEqualTo(operateStartDate);
  }
}
