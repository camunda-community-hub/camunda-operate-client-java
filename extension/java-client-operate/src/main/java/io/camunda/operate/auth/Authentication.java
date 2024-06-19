package io.camunda.operate.auth;

import java.util.Map;

public interface Authentication {

  Map.Entry<String, String> getTokenHeader();

  void resetToken();
}
