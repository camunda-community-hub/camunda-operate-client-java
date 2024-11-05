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
import io.camunda.zeebe.model.bpmn.BpmnModelInstance;
import java.util.List;

public interface CamundaOperateClient {
  ProcessDefinition getProcessDefinition(Long key) throws OperateException;

  BpmnModelInstance getProcessDefinitionModel(Long key) throws OperateException;

  String getProcessDefinitionXml(Long key) throws OperateException;

  List<ProcessDefinition> searchProcessDefinitions(SearchQuery query) throws OperateException;

  SearchResult<ProcessDefinition> searchProcessDefinitionResults(SearchQuery query)
      throws OperateException;

  List<DecisionDefinition> searchDecisionDefinitions(SearchQuery query) throws OperateException;

  SearchResult<DecisionDefinition> searchDecisionDefinitionResults(SearchQuery query)
      throws OperateException;

  List<DecisionInstance> searchDecisionInstances(SearchQuery query) throws OperateException;

  SearchResult<DecisionInstance> searchDecisionInstanceResults(SearchQuery query)
      throws OperateException;

  List<FlowNodeInstance> searchFlowNodeInstances(SearchQuery query) throws OperateException;

  SearchResult<FlowNodeInstance> searchFlowNodeInstanceResults(SearchQuery query)
      throws OperateException;

  List<Variable> searchVariables(SearchQuery query) throws OperateException;

  SearchResult<Variable> searchVariableResults(SearchQuery query) throws OperateException;

  List<ProcessInstance> searchProcessInstances(SearchQuery query) throws OperateException;

  SearchResult<ProcessInstance> searchProcessInstanceResults(SearchQuery query)
      throws OperateException;

  List<DecisionRequirements> searchDecisionRequirements(SearchQuery query) throws OperateException;

  SearchResult<DecisionRequirements> searchDecisionRequirementsResults(SearchQuery query)
      throws OperateException;

  List<Incident> searchIncidents(SearchQuery query) throws OperateException;

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
