package io.camunda.common.auth;

import io.camunda.common.json.JsonMapper;
import io.camunda.operate.auth.JwtAuthentication;
import io.camunda.operate.auth.JwtCredential;
import io.camunda.operate.auth.TokenResponse;
import java.net.MalformedURLException;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.Map;

@Deprecated
public class SaaSAuthentication implements Authentication {
  private final JwtAuthentication authentication;
  private TokenContext tokenContext;

  public SaaSAuthentication(JwtAuthentication authentication) {
    this.authentication = authentication;
  }

  public SaaSAuthentication(JwtConfig jwtConfig, JsonMapper jsonMapper) {
    this(jwtAuthentication(jwtConfig, jsonMapper));
  }

  private static JwtAuthentication jwtAuthentication(JwtConfig jwtConfig, JsonMapper jsonMapper) {
    io.camunda.common.auth.JwtCredential jwtCredential = jwtConfig.getJwtCredential();
    JwtCredential credential;
    try {
      credential =
          new JwtCredential(
              jwtCredential.getClientId(),
              jwtCredential.getClientSecret(),
              jwtCredential.getAudience(),
              URI.create(jwtCredential.getAuthUrl()).toURL());
    } catch (MalformedURLException e) {
      throw new RuntimeException("Error while mapping jwt credential", e);
    }
    return new JwtAuthentication(
        credential, (token) -> jsonMapper.fromJson(token, TokenResponse.class));
  }

  private JwtConfig jwtConfig() {
    JwtConfig jwtConfig = new JwtConfig();
    JwtCredential jwtCredential = authentication.getJwtCredential();
    io.camunda.common.auth.JwtCredential credential =
        new io.camunda.common.auth.JwtCredential(
            jwtCredential.clientId(),
            jwtCredential.clientSecret(),
            jwtCredential.audience(),
            jwtCredential.authUrl().toString());
    jwtConfig.addProduct(Product.ZEEBE, credential);
    return jwtConfig;
  }

  @Override
  public Map<String, String> getTokenHeader(Product product) {
    return authentication.getTokenHeader();
  }

  @Override
  public void resetToken(Product product) {
    tokenContext = null;
  }

  public JwtConfig getJwtConfig() {
    return jwtConfig();
  }

  public JwtAuthentication getJwtAuthentication() {
    return authentication;
  }

  private record TokenContext(String token, LocalDateTime expiryDate) {}
}
