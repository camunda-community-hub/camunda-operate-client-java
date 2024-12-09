package io.camunda.operate.spring;

import io.camunda.client.CamundaClient;
import io.camunda.operate.CamundaOperateClient;
import io.camunda.operate.CamundaOperateClientV2;
import io.camunda.operate.spring.OperateClientConditions.OperateClientEnabledCondition;
import io.camunda.operate.spring.OperateClientConditions.OperateClientV2Condition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;

@Conditional({OperateClientV2Condition.class, OperateClientEnabledCondition.class})
public class OperateClientV2Configuration {
  @Bean
  @ConditionalOnMissingBean
  public CamundaOperateClient camundaOperateClient(CamundaClient camundaClient) {
    return new CamundaOperateClientV2(camundaClient);
  }
}
