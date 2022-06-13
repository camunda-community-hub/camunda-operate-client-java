package io.camunda.operate.beta;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.core5.http.io.entity.StringEntity;

import com.fasterxml.jackson.databind.JsonNode;

import io.camunda.operate.CamundaOperateClient;
import io.camunda.operate.beta.model.AuditTrail;
import io.camunda.operate.beta.model.AuditTrailStep;
import io.camunda.operate.beta.model.FlowNodeInstanceQueryList;
import io.camunda.operate.dto.ProcessInstance;
import io.camunda.operate.exception.OperateException;
import io.camunda.operate.util.JsonUtils;
import io.camunda.zeebe.model.bpmn.BpmnModelInstance;

/**
 * Beta client to use undocumented APIs that could not be compatible in future releases
 */
public class CamundaOperateBetaClient extends CamundaOperateClient {
    
    /**
     * working fine with SaaS version 8.0.0 or if you use the local setup without Keycloak and identity
     */
    public JsonNode getFlowNodeStates(Long processInstanceId) throws OperateException {
        String url = getOperateUrl()+"/api/process-instances/"+processInstanceId+"/flow-node-states";
        HttpGet httpPost = new HttpGet(url);
        httpPost.addHeader(getAuthHeader());
        httpPost.addHeader("content-type", "application/json");
    
        try {
           
            return JsonUtils.toJsonNode(executeQuery(httpPost));
        } catch(IOException e) {
            throw new OperateException("BETA : Error getting  FlowNodeInstances", e);
        }
    }

    /**
     * working fine with SaaS version 8.0.0 or if you use the local setup without Keycloak and identity
     */
    public JsonNode getFlowNodeInstances(Long processInstanceId) throws OperateException {
        String url = getOperateUrl()+"/api/flow-node-instances";
        FlowNodeInstanceQueryList query = new FlowNodeInstanceQueryList.Builder().processInstanceId(processInstanceId).build();
        
        HttpPost httpPost = new HttpPost(url);
        httpPost.addHeader(getAuthHeader());
        httpPost.addHeader("content-type", "application/json");
        try {
            String data = JsonUtils.toJson(query);
            httpPost.setEntity(new StringEntity(data));
            
            return JsonUtils.toJsonNode(executeQuery(httpPost));
        } catch(IOException e) {
            throw new OperateException("BETA : Error getting  FlowNodeInstances", e);
        }
    }
    
    /**
     * working fine with SaaS version 8.0.0 or if you use the local setup without Keycloak and identity
     */
    public AuditTrail getAuditTrail(Long processInstanceId) throws OperateException {

        AuditTrail auditTrail = new AuditTrail();
        ProcessInstance processInstance = getProcessInstance(processInstanceId);
        auditTrail.setProcessName(processInstance.getBpmnProcessId());
        auditTrail.setProcessVersion(processInstance.getProcessVersion());
        auditTrail.setSteps(new ArrayList<>());
        Long processDefinitionKey = processInstance.getProcessDefinitionKey();
        BpmnModelInstance bpmModel = getProcessDefinitionModel(processDefinitionKey);

        JsonNode flowNodeInstances = getFlowNodeInstances(processInstanceId);
        JsonNode flowNodesChildren = flowNodeInstances.get(""+processInstanceId).get("children");
        if (flowNodesChildren.isArray()) {
            for (JsonNode jsonNode : flowNodesChildren) {
                String type = jsonNode.get("type").asText();
                if (!type.endsWith("GATEWAY")) {
                    AuditTrailStep auditTrailStep = new AuditTrailStep();
                    String stepId = jsonNode.get("flowNodeId").asText();
                    auditTrailStep.setName(bpmModel.getModelElementById(stepId).getAttributeValue("name"));
                    auditTrailStep.setType(type);
                    String startDate = jsonNode.get("startDate").asText();
                    String endDate = jsonNode.get("endDate").asText();
                    if (!startDate.equals("null")) {
                        auditTrailStep.setStartDate(startDate.substring(0,19));
                    }
                    if (!endDate.equals("null")) {
                        auditTrailStep.setEndDate(endDate.substring(0,19));
                    } else {
                        auditTrailStep.setEndDate("pending");
                    }
                    auditTrail.getSteps().add(auditTrailStep);
                }
            }
        }
        return auditTrail;
    }
}
