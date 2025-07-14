package io.camunda.operate.auth;

import java.net.URL;

public record JwtCredential(
<<<<<<< HEAD
    String clientId, String clientSecret, String audience, URL authUrl, String scope) {}
=======
    String clientId,
    String clientSecret,
    String audience,
    URL authUrl,
    String scope,
    String resource,
    Path clientAssertionKeystorePath,
    String clientAssertionKeystorePassword,
    String clientAssertionKeystoreKeyAlias,
    String clientAssertionKeystoreKeyPassword) {

  public JwtCredential(
      String clientId, String clientSecret, String audience, URL authUrl, String scope) {
    this(clientId, clientSecret, audience, authUrl, scope, null, null, null, null, null);
  }
}
>>>>>>> 1893165 (feat: add support for OAuth2 resource parameter in JWT authentication (#269))
