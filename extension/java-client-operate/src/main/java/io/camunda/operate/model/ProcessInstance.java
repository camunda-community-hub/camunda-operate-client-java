package io.camunda.operate.model;

public class ProcessInstance {
  private Long key;
  private Long processVersion;
  private String bpmnProcessId;
  private Long parentKey;
  private Long parentFlowNodeInstanceKey;
  private OperateDate startDate;
  private OperateDate endDate;
  private ProcessInstanceState state;
  private Long processDefinitionKey;
  private String tenantId;

  public Long getKey() {
    return key;
  }

  public void setKey(Long key) {
    this.key = key;
  }

  public Long getProcessVersion() {
    return processVersion;
  }

  public void setProcessVersion(Long processVersion) {
    this.processVersion = processVersion;
  }

  public String getBpmnProcessId() {
    return bpmnProcessId;
  }

  public void setBpmnProcessId(String bpmnProcessId) {
    this.bpmnProcessId = bpmnProcessId;
  }

  public Long getParentKey() {
    return parentKey;
  }

  public void setParentKey(Long parentKey) {
    this.parentKey = parentKey;
  }

  public Long getParentFlowNodeInstanceKey() {
    return parentFlowNodeInstanceKey;
  }

  public void setParentFlowNodeInstanceKey(Long parentFlowNodeInstanceKey) {
    this.parentFlowNodeInstanceKey = parentFlowNodeInstanceKey;
  }

  public OperateDate getStartDate() {
    return startDate;
  }

  public void setStartDate(OperateDate startDate) {
    this.startDate = startDate;
  }

  public OperateDate getEndDate() {
    return endDate;
  }

  public void setEndDate(OperateDate endDate) {
    this.endDate = endDate;
  }

  public ProcessInstanceState getState() {
    return state;
  }

  public void setState(ProcessInstanceState state) {
    this.state = state;
  }

  public Long getProcessDefinitionKey() {
    return processDefinitionKey;
  }

  public void setProcessDefinitionKey(Long processDefinitionKey) {
    this.processDefinitionKey = processDefinitionKey;
  }

  public String getTenantId() {
    return tenantId;
  }

  public void setTenantId(String tenantId) {
    this.tenantId = tenantId;
  }
}
