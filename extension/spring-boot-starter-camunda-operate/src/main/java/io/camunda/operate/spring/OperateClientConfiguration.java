package io.camunda.operate.spring;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.camunda.operate.CamundaOperateClient;
import io.camunda.operate.CamundaOperateClientConfiguration;
import io.camunda.operate.auth.Authentication;
import io.camunda.operate.auth.JwtAuthentication;
import io.camunda.operate.auth.JwtCredential;
import io.camunda.operate.auth.SimpleAuthentication;
import io.camunda.operate.auth.SimpleCredential;
import io.camunda.operate.auth.TokenResponseParser;
import io.camunda.operate.auth.TokenResponseParser.JacksonTokenResponseParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@EnableConfigurationProperties({
  OperateClientConfigurationProperties.class,
})
@ConditionalOnProperty(value = "operate.client.enabled", matchIfMissing = true)
@Import(ObjectMapperConfiguration.class)
public class OperateClientConfiguration {
  private final OperateClientConfigurationProperties properties;
  private final ObjectMapper objectMapper;

  @Autowired
  public OperateClientConfiguration(
      OperateClientConfigurationProperties properties, ObjectMapper objectMapper) {
    this.properties = properties;
    this.objectMapper = objectMapper;
  }

  @Bean
  @ConditionalOnMissingBean
  public CamundaOperateClient camundaOperateClient(
      CamundaOperateClientConfiguration configuration) {
    return new CamundaOperateClient(configuration);
  }

  @Bean
  @ConditionalOnMissingBean
  public CamundaOperateClientConfiguration operateClientConfiguration(
      Authentication authentication) {
    return new CamundaOperateClientConfiguration(authentication, properties.baseUrl());
  }

  @Bean
  @ConditionalOnMissingBean
  public Authentication authentication(TokenResponseParser tokenResponseParser) {
    if (properties.profile() == null) {
      throw new IllegalStateException("'operate.client.profile' is required");
    }
    switch (properties.profile()) {
      case simple -> {
        return new SimpleAuthentication(
            new SimpleCredential(
                properties.username(),
                properties.password(),
                properties.baseUrl(),
                properties.sessionTimeout()));
      }
      case oidc, saas -> {
        return new JwtAuthentication(
            new JwtCredential(
                properties.clientId(),
                properties.clientSecret(),
                properties.audience(),
                properties.authUrl().toString()),
            tokenResponseParser);
      }
      default -> throw new IllegalStateException("Unsupported profile: " + properties.profile());
    }
  }

  @Bean
  @ConditionalOnMissingBean
  public TokenResponseParser tokenResponseParser() {
    return new JacksonTokenResponseParser(objectMapper);
  }
}
