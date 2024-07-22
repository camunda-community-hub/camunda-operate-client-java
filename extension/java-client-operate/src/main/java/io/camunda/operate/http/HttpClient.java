package io.camunda.operate.http;

import com.fasterxml.jackson.core.type.TypeReference;
import java.util.Map;

/** Interface to enable swappable http client implementations */
public interface HttpClient {

  <T> T get(TypeReference<T> responseType, Map<String, String> pathParams);

  <T, U> T post(TypeReference<T> responseType, U body);

  <T> T delete(TypeReference<T> responseType, Map<String, String> pathParams);
}
