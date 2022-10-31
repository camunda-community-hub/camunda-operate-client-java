package io.camunda.operate.beta.model;

import java.util.Arrays;
import java.util.List;

public class FlowNodeInstanceQueryList {

    private List<FlowNodeInstanceQuery> queries;

    public List<FlowNodeInstanceQuery> getQueries() {
        return queries;
    }

    public void setQueries(List<FlowNodeInstanceQuery> queries) {
        this.queries = queries;
    }
    public static class Builder {

        private Long processInstanceId;
        
        public Builder processInstanceId(Long processInstanceId) {
            this.processInstanceId = processInstanceId;
            return this;
        }

        public FlowNodeInstanceQueryList build() {
            FlowNodeInstanceQuery flowNodeInstanceQuery = new FlowNodeInstanceQuery();
            FlowNodeInstanceQueryList flowNodeInstanceQueryList = new FlowNodeInstanceQueryList();
            flowNodeInstanceQuery.setProcessInstanceId(processInstanceId);
            flowNodeInstanceQuery.setTreePath(processInstanceId);
            flowNodeInstanceQuery.setPageSize(100);
            flowNodeInstanceQueryList.queries=Arrays.asList(flowNodeInstanceQuery);
            return flowNodeInstanceQueryList;
        }
    }
}
