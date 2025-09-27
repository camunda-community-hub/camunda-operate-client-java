package io.camunda.operate.search;

public class DecisionRequirementsFilterBuilder {

  DecisionRequirementsFilter filter;

  DecisionRequirementsFilterBuilder() {
    filter = new DecisionRequirementsFilter();
  }

  public DecisionRequirementsFilterBuilder id(String id) {
    filter.setId(id);
    return this;
  }

  public DecisionRequirementsFilterBuilder key(Long key) {
    filter.setKey(key);
    return this;
  }

  public DecisionRequirementsFilterBuilder decisionRequirementsId(String decisionRequirements) {
    filter.setDecisionRequirementsId(decisionRequirements);
    return this;
  }

  public DecisionRequirementsFilterBuilder name(String name) {
    filter.setName(name);
    return this;
  }

  public DecisionRequirementsFilterBuilder version(Integer version) {
    filter.setVersion(version);
    return this;
  }

  public DecisionRequirementsFilterBuilder resourceName(String resourceName) {
    filter.setResourceName(resourceName);
    return this;
  }

  public DecisionRequirementsFilterBuilder tenantId(String tenantId) {
    filter.setTenantId(tenantId);
    return this;
  }

  public DecisionRequirementsFilter build() {
    return filter;
  }
}
