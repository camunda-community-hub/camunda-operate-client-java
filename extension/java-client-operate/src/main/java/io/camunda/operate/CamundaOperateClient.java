package io.camunda.operate;

import io.camunda.operate.exception.OperateException;
import io.camunda.operate.model.ChangeStatus;
import io.camunda.operate.model.DecisionDefinition;
import io.camunda.operate.model.DecisionInstance;
import io.camunda.operate.model.DecisionRequirements;
import io.camunda.operate.model.FlowNodeInstance;
import io.camunda.operate.model.FlowNodeStatistics;
import io.camunda.operate.model.Incident;
import io.camunda.operate.model.ProcessDefinition;
import io.camunda.operate.model.ProcessInstance;
import io.camunda.operate.model.SearchResult;
import io.camunda.operate.model.Variable;
import io.camunda.operate.search.SearchQuery;
import io.camunda.zeebe.model.bpmn.Bpmn;
import io.camunda.zeebe.model.bpmn.BpmnModelInstance;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

public interface CamundaOperateClient {
  ProcessDefinition getProcessDefinition(Long key) throws OperateException;

  default BpmnModelInstance getProcessDefinitionModel(Long key) throws OperateException {
    String xml = getProcessDefinitionXml(key);
    try {
      InputStream processInputStream = new ByteArrayInputStream(xml.getBytes());
      return Bpmn.readModelFromStream(processInputStream);
    } catch (Exception e) {
      throw new OperateException(e);
    }
  }

  String getProcessDefinitionXml(Long key) throws OperateException;

  default List<ProcessDefinition> searchProcessDefinitions(SearchQuery query)
      throws OperateException {
    return searchProcessDefinitionResults(query).getItems();
  }

  SearchResult<ProcessDefinition> searchProcessDefinitionResults(SearchQuery query)
      throws OperateException;

  default List<DecisionDefinition> searchDecisionDefinitions(SearchQuery query)
      throws OperateException {
    return searchDecisionDefinitionResults(query).getItems();
  }

  SearchResult<DecisionDefinition> searchDecisionDefinitionResults(SearchQuery query)
      throws OperateException;

  default List<DecisionInstance> searchDecisionInstances(SearchQuery query)
      throws OperateException {
    return searchDecisionInstanceResults(query).getItems();
  }

  SearchResult<DecisionInstance> searchDecisionInstanceResults(SearchQuery query)
      throws OperateException;

  default List<FlowNodeInstance> searchFlowNodeInstances(SearchQuery query)
      throws OperateException {
    return searchFlowNodeInstanceResults(query).getItems();
  }

  SearchResult<FlowNodeInstance> searchFlowNodeInstanceResults(SearchQuery query)
      throws OperateException;

  default List<Variable> searchVariables(SearchQuery query) throws OperateException {
    return searchVariableResults(query).getItems();
  }

  SearchResult<Variable> searchVariableResults(SearchQuery query) throws OperateException;

  default List<ProcessInstance> searchProcessInstances(SearchQuery query) throws OperateException {
    return searchProcessInstanceResults(query).getItems();
  }

  SearchResult<ProcessInstance> searchProcessInstanceResults(SearchQuery query)
      throws OperateException;

  default List<DecisionRequirements> searchDecisionRequirements(SearchQuery query)
      throws OperateException {
    return searchDecisionRequirementsResults(query).getItems();
  }

  SearchResult<DecisionRequirements> searchDecisionRequirementsResults(SearchQuery query)
      throws OperateException;

  default List<Incident> searchIncidents(SearchQuery query) throws OperateException {
    return searchIncidentResults(query).getItems();
  }

  SearchResult<Incident> searchIncidentResults(SearchQuery query) throws OperateException;

  ProcessInstance getProcessInstance(Long key) throws OperateException;

  ChangeStatus deleteProcessInstance(Long key) throws OperateException;

  List<FlowNodeStatistics> getFlowNodeStatistics(Long key) throws OperateException;

  List<String> getSequenceFlows(Long key) throws OperateException;

  FlowNodeInstance getFlowNodeInstance(Long key) throws OperateException;

  Incident getIncident(Long key) throws OperateException;

  DecisionDefinition getDecisionDefinition(Long key) throws OperateException;

  DecisionRequirements getDecisionRequirements(Long key) throws OperateException;

  String getDecisionRequirementsXml(Long key) throws OperateException;

  DecisionInstance getDecisionInstance(String id) throws OperateException;

  Variable getVariable(Long key) throws OperateException;
}
