package io.camunda.operate;

import io.camunda.client.CamundaClient;
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

public class CamundaOperateClientV2 implements CamundaOperateClient {
  private final CamundaClient camundaClient;

  public CamundaOperateClientV2(CamundaClient camundaClient) {
    this.camundaClient = camundaClient;
  }

  @Override
  public ProcessDefinition getProcessDefinition(Long key) throws OperateException {
    throw new OperateException("Not implemented");
  }

  @Override
  public BpmnModelInstance getProcessDefinitionModel(Long key) throws OperateException {
    throw new OperateException("Not implemented");
  }

  @Override
  public String getProcessDefinitionXml(Long key) throws OperateException {
    throw new OperateException("Not implemented");
  }

  @Override
  public List<ProcessDefinition> searchProcessDefinitions(SearchQuery query)
      throws OperateException {
    throw new OperateException("Not implemented");
  }

  @Override
  public SearchResult<ProcessDefinition> searchProcessDefinitionResults(SearchQuery query)
      throws OperateException {
    throw new OperateException("Not implemented");
  }

  @Override
  public List<DecisionDefinition> searchDecisionDefinitions(SearchQuery query)
      throws OperateException {
    throw new OperateException("Not implemented");
  }

  @Override
  public SearchResult<DecisionDefinition> searchDecisionDefinitionResults(SearchQuery query)
      throws OperateException {
    throw new OperateException("Not implemented");
  }

  @Override
  public List<DecisionInstance> searchDecisionInstances(SearchQuery query) throws OperateException {
    throw new OperateException("Not implemented");
  }

  @Override
  public SearchResult<DecisionInstance> searchDecisionInstanceResults(SearchQuery query)
      throws OperateException {
    throw new OperateException("Not implemented");
  }

  @Override
  public List<FlowNodeInstance> searchFlowNodeInstances(SearchQuery query) throws OperateException {
    throw new OperateException("Not implemented");
  }

  @Override
  public SearchResult<FlowNodeInstance> searchFlowNodeInstanceResults(SearchQuery query)
      throws OperateException {
    throw new OperateException("Not implemented");
  }

  @Override
  public List<Variable> searchVariables(SearchQuery query) throws OperateException {
    throw new OperateException("Not implemented");
  }

  @Override
  public SearchResult<Variable> searchVariableResults(SearchQuery query) throws OperateException {
    throw new OperateException("Not implemented");
  }

  @Override
  public List<ProcessInstance> searchProcessInstances(SearchQuery query) throws OperateException {
    throw new OperateException("Not implemented");
  }

  @Override
  public SearchResult<ProcessInstance> searchProcessInstanceResults(SearchQuery query)
      throws OperateException {
    throw new OperateException("Not implemented");
  }

  @Override
  public List<DecisionRequirements> searchDecisionRequirements(SearchQuery query)
      throws OperateException {
    throw new OperateException("Not implemented");
  }

  @Override
  public SearchResult<DecisionRequirements> searchDecisionRequirementsResults(SearchQuery query)
      throws OperateException {
    throw new OperateException("Not implemented");
  }

  @Override
  public List<Incident> searchIncidents(SearchQuery query) throws OperateException {
    throw new OperateException("Not implemented");
  }

  @Override
  public SearchResult<Incident> searchIncidentResults(SearchQuery query) throws OperateException {
    throw new OperateException("Not implemented");
  }

  @Override
  public ProcessInstance getProcessInstance(Long key) throws OperateException {
    throw new OperateException("Not implemented");
  }

  @Override
  public ChangeStatus deleteProcessInstance(Long key) throws OperateException {
    throw new OperateException("Not implemented");
  }

  @Override
  public List<FlowNodeStatistics> getFlowNodeStatistics(Long key) throws OperateException {
    throw new OperateException("Not implemented");
  }

  @Override
  public List<String> getSequenceFlows(Long key) throws OperateException {
    throw new OperateException("Not implemented");
  }

  @Override
  public FlowNodeInstance getFlowNodeInstance(Long key) throws OperateException {
    throw new OperateException("Not implemented");
  }

  @Override
  public Incident getIncident(Long key) throws OperateException {
    throw new OperateException("Not implemented");
  }

  @Override
  public DecisionDefinition getDecisionDefinition(Long key) throws OperateException {
    throw new OperateException("Not implemented");
  }

  @Override
  public DecisionRequirements getDecisionRequirements(Long key) throws OperateException {
    throw new OperateException("Not implemented");
  }

  @Override
  public String getDecisionRequirementsXml(Long key) throws OperateException {
    throw new OperateException("Not implemented");
  }

  @Override
  public DecisionInstance getDecisionInstance(String id) throws OperateException {
    throw new OperateException("Not implemented");
  }

  @Override
  public Variable getVariable(Long key) throws OperateException {
    throw new OperateException("Not implemented");
  }
}
