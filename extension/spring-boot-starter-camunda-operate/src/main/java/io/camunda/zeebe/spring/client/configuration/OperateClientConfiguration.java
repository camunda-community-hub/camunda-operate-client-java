package io.camunda.zeebe.spring.client.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.camunda.common.auth.Authentication;
import io.camunda.operate.CamundaOperateClient;
import io.camunda.operate.CamundaOperateClientConfiguration;
import io.camunda.zeebe.spring.client.properties.CamundaClientProperties;
import io.camunda.zeebe.spring.client.properties.OperateClientConfigurationProperties;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;

@Deprecated
public class OperateClientConfiguration {
  private final OperateClientConfigurationProperties legacyProperties;
  private final CamundaClientProperties properties;
  private final Authentication authentication;

  public OperateClientConfiguration(
      OperateClientConfigurationProperties legacyProperties,
      CamundaClientProperties properties,
      Authentication authentication) {
    this.legacyProperties = legacyProperties;
    this.properties = properties;
    this.authentication = authentication;
  }

  public CamundaOperateClient camundaOperateClient() {
    io.camunda.operate.auth.Authentication auth =
        (io.camunda.operate.auth.Authentication) authentication;
    URL baseUrl;
    try {
      baseUrl = URI.create(operateUrl()).toURL();
    } catch (MalformedURLException e) {
      throw new RuntimeException("Error while reading Operate url " + operateUrl(), e);
    }
    ObjectMapper objectMapper = new ObjectMapper();
    CloseableHttpClient httpClient = HttpClients.createDefault();
    CamundaOperateClientConfiguration configuration =
        new CamundaOperateClientConfiguration(auth, baseUrl, objectMapper, httpClient);
    return new CamundaOperateClient(configuration);
  }

  private String operateUrl() {
    // TODO actually return something
    if (properties != null
        && properties.operate() != null
        && properties.operate().baseUrl() != null) {
      return properties.operate().baseUrl();
    }
    return legacyProperties.baseUrl();
  }
}
