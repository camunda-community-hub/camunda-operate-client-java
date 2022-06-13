package io.camunda.operate.beta.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class AuditTrail {
    
    private String processName;
    private Long processVersion;
    public String getProcessName() {
        return processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }

    public Long getProcessVersion() {
        return processVersion;
    }

    public void setProcessVersion(Long processVersion) {
        this.processVersion = processVersion;
    }

    private List<AuditTrailStep> steps;

    public List<AuditTrailStep> getSteps() {
        return steps;
    }

    public void setSteps(List<AuditTrailStep> steps) {
        this.steps = steps;
    }
    
}
