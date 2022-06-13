package io.camunda.operate.dto;

public class ProcessDefinition {
    private Long key;
    private String name;
    private Long version;
    private String bpmnProcessId;
    public Long getKey() {
        return key;
    }
    public void setKey(Long key) {
        this.key = key;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Long getVersion() {
        return version;
    }
    public void setVersion(Long version) {
        this.version = version;
    }
    public String getBpmnProcessId() {
        return bpmnProcessId;
    }
    public void setBpmnProcessId(String bpmnProcessId) {
        this.bpmnProcessId = bpmnProcessId;
    }
}
