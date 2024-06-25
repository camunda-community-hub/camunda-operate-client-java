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
      "operate.client.profile=saas",
      "operate.client.cluster-id=abc",
      "operate.client.region=bru-2",
      "operate.client.client-id=def",
      "operate.client.client-secret=ghi"
    })
public class OperateClientConfigurationPropertiesProfileSaasTest {
  @Autowired OperateClientConfigurationProperties properties;

  @Test
  void shouldApplyProfiles() throws MalformedURLException {
    assertThat(properties.profile()).isEqualTo(saas);
    assertThat(properties.clientId()).isEqualTo("def");
    assertThat(properties.clientSecret()).isEqualTo("ghi");
    assertThat(properties.baseUrl())
        .isEqualTo(URI.create("https://bru-2.operate.camunda.io/abc").toURL());
    assertThat(properties.enabled()).isEqualTo(true);
    assertThat(properties.authUrl())
        .isEqualTo(URI.create("https://login.cloud.camunda.io/oauth/token").toURL());
  }
}
