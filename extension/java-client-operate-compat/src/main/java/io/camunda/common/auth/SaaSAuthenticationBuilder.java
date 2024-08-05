package io.camunda.common.auth;

import io.camunda.common.json.JsonMapper;
import io.camunda.common.json.SdkObjectMapper;

@Deprecated
public class SaaSAuthenticationBuilder {
  private JsonMapper jsonMapper;
  private JwtConfig jwtConfig;

  public SaaSAuthenticationBuilder withJsonMapper(JsonMapper jsonMapper) {
    this.jsonMapper = jsonMapper;
    return this;
  }

  public SaaSAuthenticationBuilder withJwtConfig(JwtConfig jwtConfig) {
    this.jwtConfig = jwtConfig;
    return this;
  }

  public Authentication build() {
    if (jsonMapper == null) {
      jsonMapper = new SdkObjectMapper();
    }
    if (jwtConfig == null) {
      throw new IllegalArgumentException("jwtConfig is required");
    }
    return new SaaSAuthentication(jwtConfig, jsonMapper);
  }
}
