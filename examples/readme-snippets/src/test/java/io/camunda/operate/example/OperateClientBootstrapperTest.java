package io.camunda.operate.example;

import static org.assertj.core.api.Assertions.*;

import io.camunda.operate.CamundaOperateClient;
import io.camunda.operate.example.OperateClientBootstrapper.IdentityAuthOperateClientBootstrapper;
import io.camunda.operate.example.OperateClientBootstrapper.SaasClientBootstrapper;
import io.camunda.operate.example.OperateClientBootstrapper.SimpleAuthOperateClientBootstrapper;
import java.net.MalformedURLException;
import org.junit.jupiter.api.Test;

public class OperateClientBootstrapperTest {
  @Test
  void shouldBootstrapSimpleAuthClient() throws MalformedURLException {
    OperateClientBootstrapper bootstrapper = new SimpleAuthOperateClientBootstrapper();
    CamundaOperateClient client = bootstrapper.createOperateClient();
    assertThat(client).isNotNull();
  }

  @Test
  void shouldBootstrapIdentityAuthClient() throws MalformedURLException {
    OperateClientBootstrapper bootstrapper = new IdentityAuthOperateClientBootstrapper();
    CamundaOperateClient client = bootstrapper.createOperateClient();
    assertThat(client).isNotNull();
  }

  @Test
  void shouldBootstrapSaasClient() throws MalformedURLException {
    OperateClientBootstrapper bootstrapper = new SaasClientBootstrapper();
    CamundaOperateClient client = bootstrapper.createOperateClient();
    assertThat(client).isNotNull();
  }
}
