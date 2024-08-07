package io.camunda.operate;

import static io.camunda.operate.model.TypeReferences.*;

import com.fasterxml.jackson.core.type.TypeReference;
import io.camunda.operate.exception.OperateException;
import io.camunda.operate.http.DefaultHttpClient;
import io.camunda.operate.http.HttpClient;
import io.camunda.operate.model.*;
import io.camunda.operate.search.DecisionDefinitionFilter;
import io.camunda.operate.search.DecisionInstanceFilter;
import io.camunda.operate.search.DecisionRequirementsFilter;
import io.camunda.operate.search.Filter;
import io.camunda.operate.search.FlowNodeInstanceFilter;
import io.camunda.operate.search.IncidentFilter;
import io.camunda.operate.search.ProcessDefinitionFilter;
import io.camunda.operate.search.ProcessInstanceFilter;
import io.camunda.operate.search.SearchQuery;
import io.camunda.operate.search.VariableFilter;
import io.camunda.zeebe.model.bpmn.Bpmn;
import io.camunda.zeebe.model.bpmn.BpmnModelInstance;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CamundaOperateClient {

  private final HttpClient httpClient;

  private CamundaOperateClient(HttpClient httpClient) {
    this.httpClient = httpClient;
  }

  public CamundaOperateClient(CamundaOperateClientConfiguration configuration) {
    this(buildOperateHttpClient(configuration));
  }

  private static HttpClient buildOperateHttpClient(
      CamundaOperateClientConfiguration configuration) {
    // load the config map
    Map<TypeReference<?>, String> map = new HashMap<>();
    // process definitions
    map.put(searchProcessDefinition, "/process-definitions/search");
    map.put(processDefinition, "/process-definitions/{key}");
    map.put(processDefinitionXml, "/process-definitions/{key}/xml");
    // decision definition
    map.put(searchDecisionDefinition, "/decision-definitions/search");
    map.put(decisionDefinition, "/decision-definitions/{key}");
    // decision instance
    map.put(searchDecisionInstance, "/decision-instances/search");
    map.put(decisionInstance, "/decision-instances/{id}");
    // flownode instance
    map.put(searchFlowNodeInstance, "/flownode-instances/search");
    map.put(flowNodeInstance, "/flownode-instances/{key}");
    // variable
    map.put(searchVariable, "/variables/search");
    map.put(variable, "/variables/{key}");
    // process instances
    map.put(searchProcessInstance, "/process-instances/search");
    map.put(processInstance, "/process-instances/{key}");
    map.put(deleteProcessInstance, "/process-instances/{key}");
    map.put(flownodeStatistics, "/process-instances/{key}/statistics");
    map.put(sequenceFlows, "/process-instances/{key}/sequence-flows");
    // decision requirements
    map.put(searchDecisionRequirements, "/drd/search");
    map.put(decisionRequirements, "/drd/{key}");
    map.put(decisionRequirementsXml, "/drd/{key}/xml");
    // incident
    map.put(searchIncident, "/incidents/search");
    map.put(incident, "/incidents/{key}");

    try {
      return new DefaultHttpClient(
          URI.create(formatUrl(configuration.baseUrl().toString() + "/v1")).toURL(),
          configuration.authentication(),
          configuration.httpClient(),
          configuration.objectMapper(),
          map);
    } catch (MalformedURLException e) {
      throw new RuntimeException("Error while initializing operate http client", e);
    }
  }

  private static String formatUrl(String url) {
    if (url.endsWith("/")) {
      return url.substring(0, url.length() - 1);
    }
    return url;
  }

  public ProcessDefinition getProcessDefinition(Long key) throws OperateException {
    return httpClient.get(processDefinition, key(key));
  }

  public BpmnModelInstance getProcessDefinitionModel(Long key) throws OperateException {
    String xml = getProcessDefinitionXml(key);
    try {
      InputStream processInputStream = new ByteArrayInputStream(xml.getBytes());
      return Bpmn.readModelFromStream(processInputStream);
    } catch (Exception e) {
      throw new OperateException(e);
    }
  }

  public String getProcessDefinitionXml(Long key) throws OperateException {
    return httpClient.get(processDefinitionXml, key(key));
  }

  public List<ProcessDefinition> searchProcessDefinitions(SearchQuery query)
      throws OperateException {
    return searchProcessDefinitionResults(query).getItems();
  }

  public SearchResult<ProcessDefinition> searchProcessDefinitionResults(
      SearchQuery query) throws OperateException {
    assertSearchQueryType(ProcessDefinitionFilter.class,query);
    return httpClient.post(searchProcessDefinition, query);
  }

  public List<DecisionDefinition> searchDecisionDefinitions(SearchQuery query)
      throws OperateException {
    return searchDecisionDefinitionResults(query).getItems();
  }

  public SearchResult<DecisionDefinition> searchDecisionDefinitionResults(
      SearchQuery query) throws OperateException {
    assertSearchQueryType(DecisionDefinitionFilter.class,query);
    return httpClient.post(searchDecisionDefinition, query);
  }

  public List<DecisionInstance> searchDecisionInstances(SearchQuery query)
      throws OperateException {
    return searchDecisionInstanceResults(query).getItems();
  }

  public SearchResult<DecisionInstance> searchDecisionInstanceResults(
      SearchQuery query) throws OperateException {
    assertSearchQueryType(DecisionInstanceFilter.class,query);
    return httpClient.post(searchDecisionInstance, query);
  }

  public List<FlowNodeInstance> searchFlowNodeInstances(SearchQuery query)
      throws OperateException {
    return searchFlowNodeInstanceResults(query).getItems();
  }

  public SearchResult<FlowNodeInstance> searchFlowNodeInstanceResults(
      SearchQuery query) throws OperateException {
    assertSearchQueryType(FlowNodeInstanceFilter.class,query);
    return httpClient.post(searchFlowNodeInstance, query);
  }

  public List<Variable> searchVariables(SearchQuery query) throws OperateException {
    return searchVariableResults(query).getItems();
  }

  public SearchResult<Variable> searchVariableResults(SearchQuery query)
      throws OperateException {
    assertSearchQueryType(VariableFilter.class,query);
    return httpClient.post(searchVariable, query);
  }

  public List<ProcessInstance> searchProcessInstances(SearchQuery query)
      throws OperateException {
    return searchProcessInstanceResults(query).getItems();
  }

  public SearchResult<ProcessInstance> searchProcessInstanceResults(
      SearchQuery query) throws OperateException {
    assertSearchQueryType(ProcessInstanceFilter.class,query);
    return httpClient.post(searchProcessInstance, query);
  }

  public List<DecisionRequirements> searchDecisionRequirements(
      SearchQuery query) throws OperateException {
    return searchDecisionRequirementsResults(query).getItems();
  }

  public SearchResult<DecisionRequirements> searchDecisionRequirementsResults(
      SearchQuery query) throws OperateException {
    assertSearchQueryType(DecisionRequirementsFilter.class,query);
    return httpClient.post(searchDecisionRequirements, query);
  }

  public List<Incident> searchIncidents(SearchQuery query) throws OperateException {
    return searchIncidentResults(query).getItems();
  }

  public SearchResult<Incident> searchIncidentResults(SearchQuery query)
      throws OperateException {
    assertSearchQueryType(IncidentFilter.class,query);
    return httpClient.post(searchIncident, query);
  }

  public ProcessInstance getProcessInstance(Long key) throws OperateException {
    return httpClient.get(processInstance, Map.of("key", String.valueOf(key)));
  }

  public ChangeStatus deleteProcessInstance(Long key) throws OperateException {
    return httpClient.delete(deleteProcessInstance, key(key));
  }

  public List<FlowNodeStatistics> getFlowNodeStatistics(Long key) throws OperateException {
    return httpClient.get(flownodeStatistics, key(key));
  }

  public List<String> getSequenceFlows(Long key) throws OperateException {
    return httpClient.get(sequenceFlows, key(key));
  }

  public FlowNodeInstance getFlowNodeInstance(Long key) throws OperateException {
    return httpClient.get(flowNodeInstance, key(key));
  }

  public Incident getIncident(Long key) throws OperateException {
    return httpClient.get(incident, key(key));
  }

  public DecisionDefinition getDecisionDefinition(Long key) throws OperateException {
    return httpClient.get(decisionDefinition, key(key));
  }

  public DecisionRequirements getDecisionRequirements(Long key) throws OperateException {
    return httpClient.get(decisionRequirements, key(key));
  }

  public String getDecisionRequirementsXml(Long key) throws OperateException {
    return httpClient.get(decisionRequirementsXml, key(key));
  }

  public DecisionInstance getDecisionInstance(String id) throws OperateException {
    return httpClient.get(decisionInstance, Map.of("id", id));
  }

  public Variable getVariable(Long key) throws OperateException {
    return httpClient.get(variable, key(key));
  }

  private Map<String, String> key(Long key) {
    return Map.of("key", String.valueOf(key));
  }

  private void assertSearchQueryType(Class<? extends Filter> type,SearchQuery query) {
    if(!query.getFilter().getClass().isAssignableFrom(type)) {
      throw new IllegalStateException("Expected filter of type " + type + " but got " + query.getFilter().getClass());
    }
  }
}
