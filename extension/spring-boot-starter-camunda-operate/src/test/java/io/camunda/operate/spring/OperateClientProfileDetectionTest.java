package io.camunda.operate.spring;

import static io.camunda.operate.spring.OperateClientConfigurationProperties.Profile.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

public class OperateClientProfileDetectionTest {
  @Nested
  @SpringBootTest(properties = "operate.client.username=demo")
  class DetectSimpleByUsername {
    @Autowired OperateClientConfigurationProperties properties;

    @Test
    void shouldDetectProfile() {
      assertThat(properties.profile()).isEqualTo(simple);
    }
  }

  @Nested
  @SpringBootTest(properties = "operate.client.password=demo")
  class DetectSimpleByPassword {
    @Autowired OperateClientConfigurationProperties properties;

    @Test
    void shouldDetectProfile() {
      assertThat(properties.profile()).isEqualTo(simple);
    }
  }

  @Nested
  @SpringBootTest(properties = "operate.client.client-id=demo")
  class DetectOidcByClientId {
    @Autowired OperateClientConfigurationProperties properties;

    @Test
    void shouldDetectProfile() {
      assertThat(properties.profile()).isEqualTo(oidc);
    }
  }

  @Nested
  @SpringBootTest(properties = "operate.client.client-secret=demo")
  class DetectOidcByClientSecret {
    @Autowired OperateClientConfigurationProperties properties;

    @Test
    void shouldDetectProfile() {
      assertThat(properties.profile()).isEqualTo(oidc);
    }
  }

  @Nested
  @SpringBootTest(properties = {"operate.client.cluster-id=demo", "operate.client.region=bru-2"})
  class DetectSaasByClusterId {
    @Autowired OperateClientConfigurationProperties properties;

    @Test
    void shouldDetectProfile() {
      assertThat(properties.profile()).isEqualTo(saas);
    }
  }
}
