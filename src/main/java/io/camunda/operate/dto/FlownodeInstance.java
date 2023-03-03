package io.camunda.operate.dto;

import java.util.Date;

public class FlownodeInstance { 
    private Long key;
    private Long processInstanceKey;
    private Date startDate;
    private Date endDate;
    private String flowNodeId;
    private Long incidentKey;
    private String type;
    private FlownodeInstanceState state;
    private Boolean incident;
    public Long getKey() {
        return key;
    }
    public void setKey(Long key) {
        this.key = key;
    }
    public Long getProcessInstanceKey() {
        return processInstanceKey;
    }
    public void setProcessInstanceKey(Long processInstanceKey) {
        this.processInstanceKey = processInstanceKey;
    }
    public Date getStartDate() {
        return startDate;
    }
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }
    public Date getEndDate() {
        return endDate;
    }
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
    public String getFlowNodeId() {
      return flowNodeId;
    }
    public void setFlowNodeId(String flowNodeId) {
      this.flowNodeId = flowNodeId;
    }
    public Long getIncidentKey() {
        return incidentKey;
    }
    public void setIncidentKey(Long incidentKey) {
        this.incidentKey = incidentKey;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public FlownodeInstanceState getState() {
        return state;
    }
    public void setState(FlownodeInstanceState state) {
        this.state = state;
    }
    public Boolean getIncident() {
        return incident;
    }
    public void setIncident(Boolean incident) {
        this.incident = incident;
    }

}
