package io.camunda.operate.auth;

import java.util.Map;

public interface Authentication {

  Map<String, String> getTokenHeader();

  void resetToken();
}
