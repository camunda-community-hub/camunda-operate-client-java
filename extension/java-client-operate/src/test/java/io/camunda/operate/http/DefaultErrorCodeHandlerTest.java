package io.camunda.operate.http;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpEntity;
import org.junit.jupiter.api.Test;

public class DefaultErrorCodeHandlerTest {

  @Test
  void shouldReadError() throws IOException {
    String body =
"""
{ "message": "This is an error!"}
""";
    InputStream in = new ByteArrayInputStream(body.getBytes());
    HttpEntity entity = mock(HttpEntity.class);
    when(entity.getContent()).thenReturn(in);
    ClassicHttpResponse response = mock(ClassicHttpResponse.class);
    when(response.getEntity()).thenReturn(entity);
    when(response.getCode()).thenReturn(400);
    DefaultErrorCodeHandler defaultErrorCodeHandler = new DefaultErrorCodeHandler();
    RuntimeException runtimeException = defaultErrorCodeHandler.handleError(response);
    assertThat(runtimeException.getMessage())
        .isEqualTo(
            "Unsuccessful response: Code 400, body: { \"message\": \"This is an error!\"}\n");
  }

  @Test
  void shouldAppendReasonPhrase() throws IOException {
    String body =
"""
{ "message": "This is an error!"}
""";
    InputStream in = new ByteArrayInputStream(body.getBytes());
    HttpEntity entity = mock(HttpEntity.class);
    when(entity.getContent()).thenReturn(in);
    ClassicHttpResponse response = mock(ClassicHttpResponse.class);
    when(response.getEntity()).thenReturn(entity);
    when(response.getCode()).thenReturn(400);
    when(response.getReasonPhrase()).thenReturn("Mocked reason phrase");
    DefaultErrorCodeHandler defaultErrorCodeHandler = new DefaultErrorCodeHandler();
    RuntimeException runtimeException = defaultErrorCodeHandler.handleError(response);
    assertThat(runtimeException.getMessage())
        .isEqualTo(
            "Unsuccessful response: Code 400 Mocked reason phrase, body: { \"message\": \"This is an error!\"}\n");
  }

  @Test
  void shouldOmitNullBody() throws IOException {
    HttpEntity entity = mock(HttpEntity.class);
    when(entity.getContent()).thenReturn(null);
    ClassicHttpResponse response = mock(ClassicHttpResponse.class);
    when(response.getEntity()).thenReturn(entity);
    when(response.getCode()).thenReturn(400);
    when(response.getReasonPhrase()).thenReturn("Mocked reason phrase");
    DefaultErrorCodeHandler defaultErrorCodeHandler = new DefaultErrorCodeHandler();
    RuntimeException runtimeException = defaultErrorCodeHandler.handleError(response);
    assertThat(runtimeException.getMessage())
        .isEqualTo("Unsuccessful response: Code 400 Mocked reason phrase");
  }
}
