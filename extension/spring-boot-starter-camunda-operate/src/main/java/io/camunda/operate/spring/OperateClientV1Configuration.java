package io.camunda.operate.spring;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.camunda.operate.CamundaOperateClient;
import io.camunda.operate.CamundaOperateClientConfiguration;
import io.camunda.operate.CamundaOperateClientV1;
import io.camunda.operate.auth.Authentication;
import io.camunda.operate.auth.JwtAuthentication;
import io.camunda.operate.auth.JwtCredential;
import io.camunda.operate.auth.SimpleAuthentication;
import io.camunda.operate.auth.SimpleCredential;
import io.camunda.operate.http.TypeReferenceHttpClientResponseHandler;
import io.camunda.operate.spring.OperateClientConditions.OperateClientEnabledCondition;
import io.camunda.operate.spring.OperateClientConditions.OperateClientV1Condition;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Import;

@EnableConfigurationProperties({OperateClientConfigurationProperties.class})
@Conditional({OperateClientV1Condition.class, OperateClientEnabledCondition.class})
@Import(ObjectMapperConfiguration.class)
public class OperateClientV1Configuration {
  private static final Logger LOG = LoggerFactory.getLogger(OperateClientConfiguration.class);
  private final OperateClientConfigurationProperties properties;
  private final ObjectMapper objectMapper;

  @Autowired
  public OperateClientV1Configuration(
      OperateClientConfigurationProperties properties, ObjectMapper objectMapper) {
    this.properties = properties;
    this.objectMapper = objectMapper;
  }

  @Bean
  @ConditionalOnMissingBean
  public CamundaOperateClient camundaOperateClient(
      CamundaOperateClientConfiguration configuration) {
    return new CamundaOperateClientV1(configuration);
  }

  @Bean
  @ConditionalOnMissingBean
  public CamundaOperateClientConfiguration operateClientConfiguration(
      Authentication authentication,
      @Qualifier("operateHttpClient") CloseableHttpClient operateHttpClient) {
    return new CamundaOperateClientConfiguration(
        authentication, properties.baseUrl(), objectMapper, operateHttpClient);
  }

  @Bean("operateHttpClient")
  @ConditionalOnMissingBean(name = "operateHttpClient")
  public CloseableHttpClient operateHttpClient() {
    return HttpClients.createSystem();
  }

  @Bean
  @ConditionalOnMissingBean
  public Authentication operateAuthentication() {
    if (properties.profile() == null) {
      throw new IllegalStateException("Property 'operate.client.profile' is required");
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
                properties.authUrl(),
                properties.scope(),
                properties.resource(),
                properties.clientAssertionKeystorePath(),
                properties.clientAssertionKeystorePassword(),
                properties.clientAssertionKeystoreKeyAlias(),
                properties.clientAssertionKeystoreKeyPassword()),
            new TypeReferenceHttpClientResponseHandler<>(new TypeReference<>() {}, objectMapper));
      }
      default -> throw new IllegalStateException("Unsupported profile: " + properties.profile());
    }
  }
}
