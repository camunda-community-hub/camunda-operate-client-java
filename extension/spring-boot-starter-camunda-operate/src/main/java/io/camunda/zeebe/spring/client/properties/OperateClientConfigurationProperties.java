package io.camunda.zeebe.spring.client.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@Deprecated
@ConfigurationProperties("camunda.operate.client")
public record OperateClientConfigurationProperties(String baseUrl, String authUrl) {

  public String getBaseUrl() {
    return baseUrl;
  }

  public String getAuthUrl() {
    return authUrl;
  }
}
