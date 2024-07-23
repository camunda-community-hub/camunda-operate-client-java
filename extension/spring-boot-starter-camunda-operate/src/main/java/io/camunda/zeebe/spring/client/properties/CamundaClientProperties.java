package io.camunda.zeebe.spring.client.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@Deprecated
@ConfigurationProperties("camunda.client")
public record CamundaClientProperties(Operate operate) {
  public record Operate(String baseUrl) {}
}
