package io.camunda.common.auth;

@Deprecated
public record JwtCredential(String clientId, String clientSecret, String audience, String authUrl) {

  public String getClientId() {
    return clientId;
  }

  public String getClientSecret() {
    return clientSecret;
  }

  public String getAudience() {
    return audience;
  }

  public String getAuthUrl() {
    return authUrl;
  }
}
