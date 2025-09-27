package io.camunda.operate.search;

import io.camunda.operate.model.OperateDate;
import io.camunda.operate.model.ProcessInstanceState;

public class ProcessInstanceFilterBuilder {

  ProcessInstanceFilter filter;

  ProcessInstanceFilterBuilder() {
    filter = new ProcessInstanceFilter();
  }

  public ProcessInstanceFilterBuilder key(Long key) {
    filter.setKey(key);
    return this;
  }

  public ProcessInstanceFilterBuilder processVersion(Integer processVersion) {
    filter.setProcessVersion(processVersion);
    return this;
  }

  public ProcessInstanceFilterBuilder bpmnProcessId(String bpmnProcessId) {
    filter.setBpmnProcessId(bpmnProcessId);
    return this;
  }

  public ProcessInstanceFilterBuilder parentKey(Long parentKey) {
    filter.setParentKey(parentKey);
    return this;
  }

  public ProcessInstanceFilterBuilder startDate(OperateDate startDate) {
    filter.setStartDate(startDate);
    return this;
  }

  public ProcessInstanceFilterBuilder endDate(OperateDate endDate) {
    filter.setEndDate(endDate);
    return this;
  }

  public ProcessInstanceFilterBuilder state(ProcessInstanceState state) {
    filter.setState(state);
    return this;
  }

  public ProcessInstanceFilterBuilder processDefinitionKey(Long processDefinitionKey) {
    filter.setProcessDefinitionKey(processDefinitionKey);
    return this;
  }

  public ProcessInstanceFilterBuilder tenantId(String tenantId) {
    filter.setTenantId(tenantId);
    return this;
  }

  public ProcessInstanceFilter build() {
    return filter;
  }
}
