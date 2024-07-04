package io.camunda.operate.model;

import com.fasterxml.jackson.core.type.TypeReference;
import java.util.List;

public interface TypeReferences {
  TypeReference<ProcessInstance> processInstance = new TypeReference<>() {};
  TypeReference<List<FlowNodeStatistics>> flownodeStatistics = new TypeReference<>() {};
  TypeReference<List<String>> sequenceFlows = new TypeReference<>() {};
  TypeReference<ProcessDefinition> processDefinition = new TypeReference<>() {};
  TypeReference<String> processDefinitionXml = new TypeReference<>() {};
  TypeReference<SearchResult<ProcessDefinition>> searchProcessDefinition = new TypeReference<>() {};
  TypeReference<SearchResult<DecisionDefinition>> searchDecisionDefinition =
      new TypeReference<>() {};
  TypeReference<DecisionDefinition> decisionDefinition = new TypeReference<>() {};
  TypeReference<SearchResult<DecisionInstance>> searchDecisionInstance = new TypeReference<>() {};
  TypeReference<DecisionInstance> decisionInstance = new TypeReference<>() {};
  TypeReference<SearchResult<FlowNodeInstance>> searchFlowNodeInstance = new TypeReference<>() {};
  TypeReference<FlowNodeInstance> flowNodeInstance = new TypeReference<>() {};
  TypeReference<SearchResult<Variable>> searchVariable = new TypeReference<>() {};
  TypeReference<Variable> variable = new TypeReference<>() {};
  TypeReference<SearchResult<ProcessInstance>> searchProcessInstance = new TypeReference<>() {};
  TypeReference<SearchResult<DecisionRequirements>> searchDecisionRequirements =
      new TypeReference<>() {};
  TypeReference<DecisionRequirements> decisionRequirements = new TypeReference<>() {};
  TypeReference<String> decisionRequirementsXml = new TypeReference<>() {};
  TypeReference<SearchResult<Incident>> searchIncident = new TypeReference<>() {};
  TypeReference<Incident> incident = new TypeReference<>() {};
  TypeReference<ChangeStatus> deleteProcessInstance = new TypeReference<>() {};
}
