package io.camunda.operate.http;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.List;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.junit.jupiter.api.Test;

public class TypeReferenceHttpClientResponseHandlerTest {
  @Test
  void shouldReturnTypedResponse() throws HttpException, IOException {
    TypeReferenceHttpClientResponseHandler<Response> handler =
        new TypeReferenceHttpClientResponseHandler<>(
            new TypeReference<>() {}, new ObjectMapper(), code -> new RuntimeException());
    ClassicHttpResponse httpResponse = mock(ClassicHttpResponse.class);
    when(httpResponse.getCode()).thenReturn(200);
    when(httpResponse.getEntity())
        .thenReturn(
            new StringEntity(
                """
            {
              "id": "x123",
              "name": "John Doe"
            }
            """));
    Response r = handler.handleResponse(httpResponse);
    assertEquals("x123", r.id());
    assertEquals("John Doe", r.name());
  }

  @Test
  void shouldReturnTypedResponseList() throws HttpException, IOException {
    TypeReferenceHttpClientResponseHandler<List<Response>> handler =
        new TypeReferenceHttpClientResponseHandler<>(
            new TypeReference<>() {}, new ObjectMapper(), code -> new RuntimeException());
    ClassicHttpResponse httpResponse = mock(ClassicHttpResponse.class);
    when(httpResponse.getCode()).thenReturn(200);
    when(httpResponse.getEntity())
        .thenReturn(
            new StringEntity(
                """
            [
              {
                "id": "x123",
                "name": "John Doe"
              },
              {
                "id": "x124",
                "name": "John Dow"
              }
            ]
            """));
    List<Response> r = handler.handleResponse(httpResponse);
    assertEquals(2, r.size());
    assertEquals("x123", r.get(0).id());
    assertEquals("John Doe", r.get(0).name());
    assertEquals("x124", r.get(1).id());
    assertEquals("John Dow", r.get(1).name());
  }

  @Test
  void shouldReturnXmlAsString() throws IOException {
    String expectedXml =
        """
            <?xml version="1.0" encoding="UTF-8"?>
            <bpmn:definitions xmlns:bpmn="http://www.omg.org/spec/BPMN/20100524/MODEL" xmlns:bpmndi="http://www.omg.org/spec/BPMN/20100524/DI" xmlns:dc="http://www.omg.org/spec/DD/20100524/DC" xmlns:di="http://www.omg.org/spec/DD/20100524/DI" xmlns:modeler="http://camunda.org/schema/modeler/1.0" id="Definitions_0zdfaq3" targetNamespace="http://bpmn.io/schema/bpmn" exporter="Camunda Modeler" exporterVersion="5.26.0" modeler:executionPlatform="Camunda Cloud" modeler:executionPlatformVersion="8.5.0">
              <bpmn:process id="Process_1arazta" isExecutable="true">
                <bpmn:startEvent id="StartEvent_1">
                  <bpmn:outgoing>Flow_0f5dngm</bpmn:outgoing>
                </bpmn:startEvent>
                <bpmn:endEvent id="Event_19geh3d">
                  <bpmn:incoming>Flow_0f5dngm</bpmn:incoming>
                </bpmn:endEvent>
                <bpmn:sequenceFlow id="Flow_0f5dngm" sourceRef="StartEvent_1" targetRef="Event_19geh3d" />
              </bpmn:process>
              <bpmndi:BPMNDiagram id="BPMNDiagram_1">
                <bpmndi:BPMNPlane id="BPMNPlane_1" bpmnElement="Process_1arazta">
                  <bpmndi:BPMNShape id="_BPMNShape_StartEvent_2" bpmnElement="StartEvent_1">
                    <dc:Bounds x="179" y="79" width="36" height="36" />
                  </bpmndi:BPMNShape>
                  <bpmndi:BPMNShape id="Event_19geh3d_di" bpmnElement="Event_19geh3d">
                    <dc:Bounds x="272" y="79" width="36" height="36" />
                  </bpmndi:BPMNShape>
                  <bpmndi:BPMNEdge id="Flow_0f5dngm_di" bpmnElement="Flow_0f5dngm">
                    <di:waypoint x="215" y="97" />
                    <di:waypoint x="272" y="97" />
                  </bpmndi:BPMNEdge>
                </bpmndi:BPMNPlane>
              </bpmndi:BPMNDiagram>
            </bpmn:definitions>
            """;
    TypeReferenceHttpClientResponseHandler<String> handler =
        new TypeReferenceHttpClientResponseHandler<>(
            new TypeReference<>() {}, new ObjectMapper(), code -> new RuntimeException());
    ClassicHttpResponse httpResponse = mock(ClassicHttpResponse.class);
    when(httpResponse.getCode()).thenReturn(200);
    when(httpResponse.getEntity()).thenReturn(new StringEntity(expectedXml));
    String r = handler.handleResponse(httpResponse);
    assertThat(r).isEqualTo(expectedXml);
  }

  record Response(String id, String name) {}
}
