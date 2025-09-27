package io.camunda.operate.spring;

import org.springframework.boot.autoconfigure.condition.AnyNestedCondition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

public class OperateClientConditions {
  static class OperateClientEnabledCondition extends AnyNestedCondition {
    OperateClientEnabledCondition() {
      super(ConfigurationPhase.PARSE_CONFIGURATION);
    }

    @ConditionalOnProperty(value = "operate.client.enabled", matchIfMissing = true)
    static class OnOperateClientEnabled {}
  }

  static class OperateClientV1Condition extends AnyNestedCondition {
    OperateClientV1Condition() {
      super(ConfigurationPhase.PARSE_CONFIGURATION);
    }

    @ConditionalOnProperty(value = "operate.client.profile", havingValue = "saas")
    static class OnOperateClientSaas {}

    @ConditionalOnProperty(value = "operate.client.profile", havingValue = "oidc")
    static class OnOperateClientOidc {}

    @ConditionalOnProperty(value = "operate.client.profile", havingValue = "simple")
    static class OnOperateClientSimple {}
  }

  static class OperateClientV2Condition extends AnyNestedCondition {
    OperateClientV2Condition() {
      super(ConfigurationPhase.PARSE_CONFIGURATION);
    }

    @ConditionalOnProperty(value = "operate.client.profile", havingValue = "v2")
    static class OnOperateClientV2 {}
  }
}
