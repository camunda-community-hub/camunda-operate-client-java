package io.camunda.operate.http;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.apache.hc.core5.http.io.entity.EntityUtils;

public class TypeReferenceHttpClientResponseHandler<T> implements HttpClientResponseHandler<T> {
  private final TypeReference<T> typeReference;
  private final ObjectMapper objectMapper;
  private final ErrorCodeHandler errorCodeHandler;

  public TypeReferenceHttpClientResponseHandler(
      TypeReference<T> typeReference,
      ObjectMapper objectMapper,
      ErrorCodeHandler errorCodeHandler) {
    this.typeReference = typeReference;
    this.objectMapper = objectMapper;
    this.errorCodeHandler = errorCodeHandler;
  }

  public TypeReferenceHttpClientResponseHandler(
      TypeReference<T> typeReference, ObjectMapper objectMapper) {
    this(typeReference, objectMapper, new DefaultErrorCodeHandler());
  }

  @Override
  public T handleResponse(ClassicHttpResponse response) throws IOException {
    T resp;
    if (200 <= response.getCode() && response.getCode() <= 299) {
      HttpEntity entity = response.getEntity();
      String tmp = new String(entity.getContent().readAllBytes(), StandardCharsets.UTF_8);
      if (typeReference.getType().equals(String.class)) {
        resp = (T) tmp;
      } else {
        resp = objectMapper.readValue(tmp, typeReference);
      }
      EntityUtils.consume(entity);
      return resp;
    } else {
      throw errorCodeHandler.handleError(response);
    }
  }
}
