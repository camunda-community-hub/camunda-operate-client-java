package io.camunda.operate.http;

import org.apache.hc.core5.http.ClassicHttpResponse;

public interface ErrorCodeHandler {
  RuntimeException handleError(ClassicHttpResponse response);
}
