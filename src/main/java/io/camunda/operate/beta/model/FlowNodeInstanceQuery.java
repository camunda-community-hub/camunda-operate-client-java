package io.camunda.operate.beta.model;

public class FlowNodeInstanceQuery {

    private Long treePath;
    
    private Long processInstanceId;
    
    private int pageSize;

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public Long getTreePath() {
        return treePath;
    }

    public void setTreePath(Long treePath) {
        this.treePath = treePath;
    }

    public Long getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(Long processInstanceId) {
        this.processInstanceId = processInstanceId;
    }
    
    
}
