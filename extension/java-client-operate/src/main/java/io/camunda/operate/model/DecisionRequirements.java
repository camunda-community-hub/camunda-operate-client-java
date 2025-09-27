package io.camunda.operate.model;

public class DecisionRequirements {

  private String id;
  private Long key;
  private String decisionRequirementsId;
  private String name;
  private Integer version;
  private String resourceName;
  private String tenantId;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Long getKey() {
    return key;
  }

  public void setKey(Long key) {
    this.key = key;
  }

  public String getDecisionRequirementsId() {
    return decisionRequirementsId;
  }

  public void setDecisionRequirementsId(String decisionRequirementsId) {
    this.decisionRequirementsId = decisionRequirementsId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Integer getVersion() {
    return version;
  }

  public void setVersion(Integer version) {
    this.version = version;
  }

  public String getResourceName() {
    return resourceName;
  }

  public void setResourceName(String resourceName) {
    this.resourceName = resourceName;
  }

  public String getTenantId() {
    return tenantId;
  }

  public void setTenantId(String tenantId) {
    this.tenantId = tenantId;
  }
}
