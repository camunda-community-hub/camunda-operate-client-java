package io.camunda.operate.http;

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

  record Response(String id, String name) {}
}
