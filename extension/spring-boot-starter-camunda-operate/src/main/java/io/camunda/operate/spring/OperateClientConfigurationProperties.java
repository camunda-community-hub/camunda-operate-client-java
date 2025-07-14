package io.camunda.operate.spring;

import java.net.URL;
import java.nio.file.Path;
import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("operate.client")
public record OperateClientConfigurationProperties(
    // generic properties
    Profile profile,
    Boolean enabled,
    URL baseUrl,
    // simple auth properties
    String username,
    String password,
    Duration sessionTimeout,
    // oidc auth properties
    String clientId,
    String clientSecret,
    URL authUrl,
    String audience,
    String scope,
    String resource,
    Path clientAssertionKeystorePath,
    String clientAssertionKeystorePassword,
    String clientAssertionKeystoreKeyAlias,
    String clientAssertionKeystoreKeyPassword,
    // saas auth properies
    String region,
    String clusterId) {
  public enum Profile {
    simple,
    oidc,
    saas
  }
}
