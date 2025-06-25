package io.camunda.operate.auth;

import java.net.URL;
import java.nio.file.Path;

public record JwtCredential(
    String clientId,
    String clientSecret,
    String audience,
    URL authUrl,
    String scope,
    Path clientAssertionKeystorePath,
    String clientAssertionKeystorePassword,
    String clientAssertionKeystoreKeyAlias,
    String clientAssertionKeystoreKeyPassword) {

  public JwtCredential(
      String clientId, String clientSecret, String audience, URL authUrl, String scope) {
    this(clientId, clientSecret, audience, authUrl, scope, null, null, null, null);
  }
}
