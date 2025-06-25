package io.camunda.operate.auth;

import java.net.URL;

public record JwtCredential(
    String clientId,
    String clientSecret,
    String audience,
    URL authUrl,
    String scope,
    String clientAssertionCertPath,
    String clientAssertionCertStorePassword) {

  public JwtCredential(
      String clientId, String clientSecret, String audience, URL authUrl, String scope) {
    this(clientId, clientSecret, audience, authUrl, scope, null, null);
  }
}
