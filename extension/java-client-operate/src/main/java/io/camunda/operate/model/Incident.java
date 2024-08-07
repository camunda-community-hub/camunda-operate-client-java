package io.camunda.operate.model;

public class Incident {
  private Long key;
  private Long processDefinitionKey;
  private Long processInstanceKey;
  private String type;
  private String message;
  private OperateDate creationTime;
  private String state;
  private String tenantId;

  public Long getKey() {
    return key;
  }

  public void setKey(Long key) {
    this.key = key;
  }

  public Long getProcessDefinitionKey() {
    return processDefinitionKey;
  }

  public void setProcessDefinitionKey(Long processDefinitionKey) {
    this.processDefinitionKey = processDefinitionKey;
  }

  public Long getProcessInstanceKey() {
    return processInstanceKey;
  }

  public void setProcessInstanceKey(Long processInstanceKey) {
    this.processInstanceKey = processInstanceKey;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public OperateDate getCreationTime() {
    return creationTime;
  }

  public void setCreationTime(OperateDate creationTime) {
    this.creationTime = creationTime;
  }

  public String getState() {
    return state;
  }

  public void setState(String state) {
    this.state = state;
  }

  public String getTenantId() {
    return tenantId;
  }

  public void setTenantId(String tenantId) {
    this.tenantId = tenantId;
  }
}
