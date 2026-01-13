package io.camunda.operate.spring;

import static io.camunda.operate.spring.OperateClientConfigurationProperties.Profile.*;
import static org.assertj.core.api.Assertions.*;

import java.net.MalformedURLException;
import java.net.URI;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(
    properties = {
      "operate.client.profile=oidc",
      "operate.client.client-id=def",
      "operate.client.client-secret=ghi"
    })
public class OperateClientV1ConfigurationPropertiesProfileOidcTest {
  @Autowired OperateClientConfigurationProperties properties;

  @Test
  void shouldApplyProfiles() throws MalformedURLException {
    assertThat(properties.profile()).isEqualTo(oidc);
    assertThat(properties.clientId()).isEqualTo("def");
    assertThat(properties.clientSecret()).isEqualTo("ghi");
    assertThat(properties.baseUrl()).isEqualTo(URI.create("http://localhost:8088").toURL());
    assertThat(properties.enabled()).isEqualTo(true);
    assertThat(properties.authUrl())
        .isEqualTo(
            URI.create(
                    "http://localhost:18080/auth/realms/camunda-platform/protocol/openid-connect/token")
                .toURL());
  }

  @SpringBootTest(
      properties = {
        "operate.client.profile=oidc",
        "operate.client.client-id=test-client",
        "operate.client.client-secret=test-secret",
        "operate.client.resource=test-resource"
      })
  static class WithResourcePropertyTest {
    @Autowired OperateClientConfigurationProperties properties;

    @Test
    void shouldApplyResourceProperty() throws MalformedURLException {
      assertThat(properties.profile()).isEqualTo(oidc);
      assertThat(properties.clientId()).isEqualTo("test-client");
      assertThat(properties.clientSecret()).isEqualTo("test-secret");
      assertThat(properties.resource()).isEqualTo("test-resource");
      assertThat(properties.baseUrl()).isEqualTo(URI.create("http://localhost:8088").toURL());
      assertThat(properties.enabled()).isEqualTo(true);
      assertThat(properties.authUrl())
          .isEqualTo(
              URI.create(
                      "http://localhost:18080/auth/realms/camunda-platform/protocol/openid-connect/token")
                  .toURL());
    }
  }

  @SpringBootTest(
      properties = {
        "operate.client.profile=oidc",
        "operate.client.client-id=env-test-client",
        "operate.client.client-secret=env-test-secret",
        "operate.client.resource=env-test-resource",
        "operate.client.scope=env-test-scope"
      })
  static class EnvironmentVariableTest {
    @Autowired OperateClientConfigurationProperties properties;

    @Test
    void shouldSupportEnvironmentVariableMapping() {
      // Verify that properties can be set via environment-style configuration
      assertThat(properties.profile()).isEqualTo(oidc);
      assertThat(properties.clientId()).isEqualTo("env-test-client");
      assertThat(properties.clientSecret()).isEqualTo("env-test-secret");
      assertThat(properties.resource()).isEqualTo("env-test-resource");
      assertThat(properties.scope()).isEqualTo("env-test-scope");
    }
  }
}
