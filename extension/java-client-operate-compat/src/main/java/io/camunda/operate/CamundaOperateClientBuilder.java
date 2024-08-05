package io.camunda.operate;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.camunda.common.auth.SaaSAuthentication;
import io.camunda.operate.auth.Authentication;
import java.net.MalformedURLException;
import java.net.URI;
import org.apache.hc.client5.http.impl.classic.HttpClients;

@Deprecated
public class CamundaOperateClientBuilder
    implements io.camunda.operate.spi.CamundaOperateClientBuilder {
  private String operateUrl;
  private io.camunda.common.auth.Authentication authentication;

  public CamundaOperateClientBuilder operateUrl(String operateUrl) {
    this.operateUrl = operateUrl;
    return this;
  }

  public CamundaOperateClientBuilder authentication(
      io.camunda.common.auth.Authentication authentication) {
    this.authentication = authentication;
    return this;
  }

  public CamundaOperateClientBuilder setup() {
    // who needs this at all???
    return this;
  }

  public CamundaOperateClient build() {
    try {
      Authentication auth = mapFromLegacy();
      CamundaOperateClientConfiguration configuration =
          new CamundaOperateClientConfiguration(
              auth,
              URI.create(operateUrl).toURL(),
              new ObjectMapper(),
              HttpClients.createDefault());
      return new CamundaOperateClient(configuration);
    } catch (MalformedURLException e) {
      throw new RuntimeException("Error while creating operate client configuration", e);
    }
  }

  private Authentication mapFromLegacy() {
    if (authentication == null) {
      throw new IllegalStateException("Authentication not set");
    }
    // there is only saas auth which is basically wrapping a jwt auth
    if (authentication instanceof SaaSAuthentication saaSAuthentication) {
      return saaSAuthentication.getJwtAuthentication();
    }
    throw new IllegalStateException(
        "Unknown authentication type: " + authentication.getClass().getName());
  }
}
