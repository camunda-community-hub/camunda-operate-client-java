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
public class OperateClientConfigurationPropertiesProfileOidcTest {
  @Autowired OperateClientConfigurationProperties properties;

  @Test
  void shouldApplyProfiles() throws MalformedURLException {
    assertThat(properties.profile()).isEqualTo(oidc);
    assertThat(properties.clientId()).isEqualTo("def");
    assertThat(properties.clientSecret()).isEqualTo("ghi");
    assertThat(properties.baseUrl()).isEqualTo(URI.create("http://localhost:8081").toURL());
    assertThat(properties.enabled()).isEqualTo(true);
    assertThat(properties.authUrl())
        .isEqualTo(
            URI.create("http://localhost:18080/auth/realms/camunda-platform/openid-connect/token")
                .toURL());
  }
}
