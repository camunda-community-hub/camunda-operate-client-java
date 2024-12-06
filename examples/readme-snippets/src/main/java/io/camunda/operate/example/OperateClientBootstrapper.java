package io.camunda.operate.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.camunda.operate.CamundaOperateClient;
import io.camunda.operate.CamundaOperateClientConfiguration;
import io.camunda.operate.auth.JwtAuthentication;
import io.camunda.operate.auth.JwtCredential;
import io.camunda.operate.auth.SimpleAuthentication;
import io.camunda.operate.auth.SimpleCredential;
import io.camunda.operate.auth.TokenResponseMapper;
import io.camunda.operate.auth.TokenResponseMapper.JacksonTokenResponseMapper;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.time.Duration;
import org.apache.hc.client5.http.impl.classic.HttpClients;

public interface OperateClientBootstrapper {
  CamundaOperateClient createOperateClient() throws MalformedURLException;

  class SimpleAuthOperateClientBootstrapper implements OperateClientBootstrapper {
    public CamundaOperateClient createOperateClient() throws MalformedURLException {
      // properties you need to provide
      String username = "demo";
      String password = "demo";
      URL operateUrl = URI.create("http://localhost:8081").toURL();
      // bootstrapping
      SimpleCredential credentials =
          new SimpleCredential(username, password, operateUrl, Duration.ofMinutes(10));
      SimpleAuthentication authentication = new SimpleAuthentication(credentials);
      ObjectMapper objectMapper = new ObjectMapper();
      CamundaOperateClientConfiguration configuration =
          new CamundaOperateClientConfiguration(
              authentication, operateUrl, objectMapper, HttpClients.createDefault());
      CamundaOperateClient client = new CamundaOperateClient(configuration);
      return client;
    }
  }

  class IdentityAuthOperateClientBootstrapper implements OperateClientBootstrapper {
    public CamundaOperateClient createOperateClient() throws MalformedURLException {
      // properties you need to provide
      String clientId = "";
      String clientSecret = "";
      String audience = "operate-api";
      String scope = ""; // can be omitted if not required
      URL operateUrl = URI.create("http://localhost:8081").toURL();
      URL authUrl =
          URI.create(
                  "http://localhost:18080/auth/realms/camunda-platform/protocol/openid-connect/token")
              .toURL();
      // bootstrapping
      JwtCredential credentials =
          new JwtCredential(clientId, clientSecret, audience, authUrl, scope);
      ObjectMapper objectMapper = new ObjectMapper();
      TokenResponseMapper tokenResponseMapper = new JacksonTokenResponseMapper(objectMapper);
      JwtAuthentication authentication = new JwtAuthentication(credentials, tokenResponseMapper);
      CamundaOperateClientConfiguration configuration =
          new CamundaOperateClientConfiguration(
              authentication, operateUrl, objectMapper, HttpClients.createDefault());
      CamundaOperateClient client = new CamundaOperateClient(configuration);
      return client;
    }
  }

  class SaasClientBootstrapper implements OperateClientBootstrapper {
    public CamundaOperateClient createOperateClient() throws MalformedURLException {
      // properties you need to provide
      String region = "";
      String clusterId = "";
      String clientId = "";
      String clientSecret = "";
      // bootstrapping
      URL operateUrl = URI.create("https://" + region + ".operate.camunda.io/" + clusterId).toURL();
      URL authUrl = URI.create("https://login.cloud.camunda.io/oauth/token").toURL();
      JwtCredential credentials =
          new JwtCredential(clientId, clientSecret, "operate.camunda.io", authUrl, null);
      ObjectMapper objectMapper = new ObjectMapper();
      TokenResponseMapper tokenResponseMapper = new JacksonTokenResponseMapper(objectMapper);
      JwtAuthentication authentication = new JwtAuthentication(credentials, tokenResponseMapper);
      CamundaOperateClientConfiguration configuration =
          new CamundaOperateClientConfiguration(
              authentication, operateUrl, objectMapper, HttpClients.createDefault());
      CamundaOperateClient client = new CamundaOperateClient(configuration);
      return client;
    }
  }
}
