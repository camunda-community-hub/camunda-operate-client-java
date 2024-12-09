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
import java.net.MalformedURLException;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CamundaOperateClientV1 implements CamundaOperateClient {

  private final HttpClient httpClient;

  private CamundaOperateClientV1(HttpClient httpClient) {
    this.httpClient = httpClient;
  }

  public CamundaOperateClientV1(CamundaOperateClientConfiguration configuration) {
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

  @Override
  public ProcessDefinition getProcessDefinition(Long key) throws OperateException {
    return httpClient.get(processDefinition, key(key));
  }

  @Override
  public String getProcessDefinitionXml(Long key) throws OperateException {
    return httpClient.get(processDefinitionXml, key(key));
  }

  @Override
  public SearchResult<ProcessDefinition> searchProcessDefinitionResults(SearchQuery query)
      throws OperateException {
    assertSearchQueryType(ProcessDefinitionFilter.class, query);
    return httpClient.post(searchProcessDefinition, query);
  }

  @Override
  public SearchResult<DecisionDefinition> searchDecisionDefinitionResults(SearchQuery query)
      throws OperateException {
    assertSearchQueryType(DecisionDefinitionFilter.class, query);
    return httpClient.post(searchDecisionDefinition, query);
  }

  @Override
  public SearchResult<DecisionInstance> searchDecisionInstanceResults(SearchQuery query)
      throws OperateException {
    assertSearchQueryType(DecisionInstanceFilter.class, query);
    return httpClient.post(searchDecisionInstance, query);
  }

  @Override
  public SearchResult<FlowNodeInstance> searchFlowNodeInstanceResults(SearchQuery query)
      throws OperateException {
    assertSearchQueryType(FlowNodeInstanceFilter.class, query);
    return httpClient.post(searchFlowNodeInstance, query);
  }

  @Override
  public SearchResult<Variable> searchVariableResults(SearchQuery query) throws OperateException {
    assertSearchQueryType(VariableFilter.class, query);
    return httpClient.post(searchVariable, query);
  }

  @Override
  public SearchResult<ProcessInstance> searchProcessInstanceResults(SearchQuery query)
      throws OperateException {
    assertSearchQueryType(ProcessInstanceFilter.class, query);
    return httpClient.post(searchProcessInstance, query);
  }

  @Override
  public SearchResult<DecisionRequirements> searchDecisionRequirementsResults(SearchQuery query)
      throws OperateException {
    assertSearchQueryType(DecisionRequirementsFilter.class, query);
    return httpClient.post(searchDecisionRequirements, query);
  }

  @Override
  public SearchResult<Incident> searchIncidentResults(SearchQuery query) throws OperateException {
    assertSearchQueryType(IncidentFilter.class, query);
    return httpClient.post(searchIncident, query);
  }

  @Override
  public ProcessInstance getProcessInstance(Long key) throws OperateException {
    return httpClient.get(processInstance, Map.of("key", String.valueOf(key)));
  }

  @Override
  public ChangeStatus deleteProcessInstance(Long key) throws OperateException {
    return httpClient.delete(deleteProcessInstance, key(key));
  }

  @Override
  public List<FlowNodeStatistics> getFlowNodeStatistics(Long key) throws OperateException {
    return httpClient.get(flownodeStatistics, key(key));
  }

  @Override
  public List<String> getSequenceFlows(Long key) throws OperateException {
    return httpClient.get(sequenceFlows, key(key));
  }

  @Override
  public FlowNodeInstance getFlowNodeInstance(Long key) throws OperateException {
    return httpClient.get(flowNodeInstance, key(key));
  }

  @Override
  public Incident getIncident(Long key) throws OperateException {
    return httpClient.get(incident, key(key));
  }

  @Override
  public DecisionDefinition getDecisionDefinition(Long key) throws OperateException {
    return httpClient.get(decisionDefinition, key(key));
  }

  @Override
  public DecisionRequirements getDecisionRequirements(Long key) throws OperateException {
    return httpClient.get(decisionRequirements, key(key));
  }

  @Override
  public String getDecisionRequirementsXml(Long key) throws OperateException {
    return httpClient.get(decisionRequirementsXml, key(key));
  }

  @Override
  public DecisionInstance getDecisionInstance(String id) throws OperateException {
    return httpClient.get(decisionInstance, Map.of("id", id));
  }

  @Override
  public Variable getVariable(Long key) throws OperateException {
    return httpClient.get(variable, key(key));
  }

  private Map<String, String> key(Long key) {
    return Map.of("key", String.valueOf(key));
  }

  private void assertSearchQueryType(Class<? extends Filter> type, SearchQuery query) {
    if (query.getFilter() != null && !query.getFilter().getClass().isAssignableFrom(type)) {
      throw new IllegalStateException(
          "Expected filter of type " + type + " but got " + query.getFilter().getClass());
    }
  }
}
