package io.camunda.operate;

import static java.util.Map.*;

import io.camunda.client.CamundaClient;
import io.camunda.client.api.response.CancelProcessInstanceResponse;
import io.camunda.client.api.response.EvaluatedDecisionInput;
import io.camunda.client.api.response.EvaluatedDecisionOutput;
import io.camunda.client.api.response.MatchedDecisionRule;
import io.camunda.client.api.search.enums.ElementInstanceState;
import io.camunda.client.api.search.enums.ElementInstanceType;
import io.camunda.client.api.search.enums.IncidentErrorType;
import io.camunda.client.api.search.enums.IncidentState;
import io.camunda.client.api.search.filter.DecisionDefinitionFilter;
import io.camunda.client.api.search.filter.DecisionInstanceFilter;
import io.camunda.client.api.search.filter.DecisionRequirementsFilter;
import io.camunda.client.api.search.filter.ElementInstanceFilter;
import io.camunda.client.api.search.filter.IncidentFilter;
import io.camunda.client.api.search.filter.ProcessDefinitionFilter;
import io.camunda.client.api.search.filter.ProcessInstanceFilter;
import io.camunda.client.api.search.filter.VariableFilter;
import io.camunda.client.api.search.request.SearchRequestPage;
import io.camunda.client.api.search.request.TypedSortableRequest.SearchRequestSort;
import io.camunda.client.api.search.response.DecisionDefinitionType;
import io.camunda.client.api.search.response.DecisionInstanceState;
import io.camunda.client.api.search.response.ElementInstance;
import io.camunda.client.api.search.response.ProcessInstanceSequenceFlow;
import io.camunda.client.api.search.response.SearchResponse;
import io.camunda.client.api.search.sort.DecisionDefinitionSort;
import io.camunda.client.api.search.sort.DecisionInstanceSort;
import io.camunda.client.api.search.sort.DecisionRequirementsSort;
import io.camunda.client.api.search.sort.ElementInstanceSort;
import io.camunda.client.api.search.sort.IncidentSort;
import io.camunda.client.api.search.sort.ProcessDefinitionSort;
import io.camunda.client.api.search.sort.ProcessInstanceSort;
import io.camunda.client.api.search.sort.VariableSort;
import io.camunda.client.api.statistics.response.ProcessElementStatistics;
import io.camunda.operate.exception.OperateException;
import io.camunda.operate.model.ChangeStatus;
import io.camunda.operate.model.DecisionDefinition;
import io.camunda.operate.model.DecisionInstance;
import io.camunda.operate.model.DecisionInstanceInput;
import io.camunda.operate.model.DecisionInstanceOutput;
import io.camunda.operate.model.DecisionRequirements;
import io.camunda.operate.model.DecisionState;
import io.camunda.operate.model.DecisionType;
import io.camunda.operate.model.FlowNodeInstance;
import io.camunda.operate.model.FlowNodeInstanceState;
import io.camunda.operate.model.FlowNodeStatistics;
import io.camunda.operate.model.Incident;
import io.camunda.operate.model.OperateDate;
import io.camunda.operate.model.ProcessDefinition;
import io.camunda.operate.model.ProcessInstance;
import io.camunda.operate.model.ProcessInstanceState;
import io.camunda.operate.model.SearchResult;
import io.camunda.operate.model.Variable;
import io.camunda.operate.search.FlowNodeInstanceFilter;
import io.camunda.operate.search.SearchQuery;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CamundaOperateClientV2 implements CamundaOperateClient {
  private static final Logger LOG = LoggerFactory.getLogger(CamundaOperateClientV2.class);
  private final CamundaClient camundaClient;

  public CamundaOperateClientV2(CamundaClient camundaClient) {
    this.camundaClient = camundaClient;
  }

  private static ProcessDefinition fromProcessDefinition(
      io.camunda.client.api.search.response.ProcessDefinition processDefinition) {
    ProcessDefinition p = new ProcessDefinition();
    p.setKey(processDefinition.getProcessDefinitionKey());
    p.setName(processDefinition.getName());
    p.setBpmnProcessId(processDefinition.getProcessDefinitionId());
    p.setTenantId(processDefinition.getTenantId());
    p.setVersion(processDefinition.getVersion());
    p.setVersionTag(processDefinition.getVersionTag());
    return p;
  }

  private static Variable fromVariable(io.camunda.client.api.search.response.Variable variable) {
    Variable v = new Variable();
    v.setName(variable.getName());
    v.setValue(variable.getValue());
    v.setKey(variable.getVariableKey());
    v.setProcessInstanceKey(variable.getProcessInstanceKey());
    v.setScopeKey(variable.getScopeKey());
    v.setTenantId(variable.getTenantId());
    v.setTruncated(variable.isTruncated());
    return v;
  }

  private static Consumer<ProcessDefinitionFilter> fromProcessDefinitionSearchToFilter(
      SearchQuery query) {
    return fromSearchQueryToFilter(
        query,
        io.camunda.operate.search.ProcessDefinitionFilter.class,
        (filter, processDefinitionFilter) -> {
          applyIfNotNull(processDefinitionFilter.getKey(), filter::processDefinitionKey);
          applyIfNotNull(processDefinitionFilter.getName(), filter::name);
          applyIfNotNull(processDefinitionFilter.getTenantId(), filter::tenantId);
          applyIfNotNull(processDefinitionFilter.getBpmnProcessId(), filter::processDefinitionId);
          applyIfNotNull(processDefinitionFilter.getVersion(), filter::version);
          applyIfNotNull(processDefinitionFilter.getVersionTag(), filter::versionTag);
        });
  }

  private static <T, F> Consumer<T> fromSearchQueryToFilter(
      SearchQuery query, Class<F> expectedType, BiConsumer<T, F> filterConsumer) {
    return filter -> {
      if (expectedType.isAssignableFrom(query.getFilter().getClass())) {
        filterConsumer.accept(filter, expectedType.cast(query.getFilter()));
      } else {
        throw new OperateException(
            "Expected "
                + expectedType.getSimpleName()
                + " but got "
                + query.getFilter().getClass().getSimpleName()
                + " instead");
      }
    };
  }

  private static <T> void applyIfNotNull(T property, Consumer<T> setter) {
    applyIfNotNull(property, o -> o, setter);
  }

  private static <T, S> void applyIfNotNull(T property, Function<T, S> mapper, Consumer<S> setter) {
    if (property != null) {
      setter.accept(mapper.apply(property));
    }
  }

  private static Consumer<ProcessDefinitionSort> fromProcessDefinitionSearchToSort(
      SearchQuery query) {
    return fromSearchQueryToSort(
        query,
        Map.ofEntries(
            entry("name", ProcessDefinitionSort::name),
            entry("version", ProcessDefinitionSort::version),
            entry("versionTag", ProcessDefinitionSort::versionTag),
            entry("tenantId", ProcessDefinitionSort::tenantId),
            entry("key", ProcessDefinitionSort::processDefinitionKey),
            entry("processDefinitionKey", ProcessDefinitionSort::processDefinitionKey),
            entry("bpmnProcessId", ProcessDefinitionSort::processDefinitionId),
            entry("processDefinitionId", ProcessDefinitionSort::processDefinitionId)));
  }

  private static <T extends SearchRequestSort<?>> Consumer<T> fromSearchQueryToSort(
      SearchQuery query, Map<String, Consumer<T>> sortKeyFieldMapper) {
    return sort ->
        query
            .getSort()
            .forEach(
                (sortKey) -> {
                  if (sortKeyFieldMapper.containsKey(sortKey.getField())) {
                    sortKeyFieldMapper.get(sortKey.getField()).accept(sort);
                    switch (sortKey.getOrder()) {
                      case ASC -> sort.asc();
                      case DESC -> sort.desc();
                    }
                  }
                });
  }

  private static SearchResult<ProcessDefinition> fromProcessDefinitionSearchResponse(
      SearchResponse<io.camunda.client.api.search.response.ProcessDefinition> searchResponse,
      SearchQuery query) {
    return fromSearchResponse(
        searchResponse, query, CamundaOperateClientV2::fromProcessDefinitions);
  }

  private static <T, S> SearchResult<T> fromSearchResponse(
      SearchResponse<S> searchResponse, SearchQuery query, Function<List<S>, List<T>> mapper) {
    SearchResult<T> searchResult = new SearchResult<>();
    searchResult.setTotal(Math.toIntExact(searchResponse.page().totalItems()));
    searchResult.setSortValues(Arrays.asList(query.getSort().toArray()));
    searchResult.setItems(mapper.apply(searchResponse.items()));
    return searchResult;
  }

  private static List<ProcessDefinition> fromProcessDefinitions(
      List<io.camunda.client.api.search.response.ProcessDefinition> processDefinitions) {
    return fromList(processDefinitions, CamundaOperateClientV2::fromProcessDefinition);
  }

  private static <T, S> List<T> fromList(List<S> list, Function<S, T> mapper) {
    if (list == null) {
      return null;
    }
    return list.stream().map(mapper).toList();
  }

  private static DecisionInstance fromDecisionInstance(
      io.camunda.client.api.search.response.DecisionInstance decisionInstance) {
    DecisionInstance instance = new DecisionInstance();
    instance.setId(decisionInstance.getDecisionInstanceId());
    instance.setKey(decisionInstance.getDecisionInstanceKey());
    instance.setState(fromDecisionInstanceState(decisionInstance.getState()));
    instance.setEvaluationDate(OperateDate.date(decisionInstance.getEvaluationDate()));
    instance.setEvaluationFailure(decisionInstance.getEvaluationFailure());
    instance.setProcessDefinitionKey(decisionInstance.getProcessDefinitionKey());
    instance.setProcessInstanceKey(decisionInstance.getProcessInstanceKey());
    instance.setTenantId(decisionInstance.getTenantId());
    instance.setDecisionDefinitionId(decisionInstance.getDecisionDefinitionId());
    instance.setDecisionName(decisionInstance.getDecisionDefinitionName());
    instance.setDecisionVersion(decisionInstance.getDecisionDefinitionVersion());
    instance.setDecisionType(
        fromDecisionDefinitionType(decisionInstance.getDecisionDefinitionType()));
    instance.setResult(decisionInstance.getResult());
    instance.setEvaluatedInputs(fromEvaluatedInputs(decisionInstance.getEvaluatedInputs()));
    instance.setEvaluatedOutputs(fromMatchedRules(decisionInstance.getMatchedRules()));
    return instance;
  }

  private static List<DecisionInstanceOutput> fromMatchedRules(
      List<MatchedDecisionRule> matchedRules) {
    if (matchedRules == null) {
      return null;
    }
    return matchedRules.stream().flatMap(CamundaOperateClientV2::fromMatchedRule).toList();
  }

  private static Stream<DecisionInstanceOutput> fromMatchedRule(
      MatchedDecisionRule matchedDecisionRule) {
    if (matchedDecisionRule == null || matchedDecisionRule.getEvaluatedOutputs() == null) {
      return Stream.empty();
    }
    return matchedDecisionRule.getEvaluatedOutputs().stream()
        .map(CamundaOperateClientV2::fromEvaluatedDecisionOutput)
        .peek(
            output -> {
              output.setRuleId(matchedDecisionRule.getRuleId());
              output.setRuleIndex(matchedDecisionRule.getRuleIndex());
            });
  }

  private static DecisionInstanceOutput fromEvaluatedDecisionOutput(
      EvaluatedDecisionOutput evaluatedDecisionOutput) {
    DecisionInstanceOutput output = new DecisionInstanceOutput();
    output.setId(evaluatedDecisionOutput.getOutputId());
    output.setName(evaluatedDecisionOutput.getOutputName());
    output.setValue(evaluatedDecisionOutput.getOutputValue());
    return output;
  }

  private static List<DecisionInstanceInput> fromEvaluatedInputs(
      List<EvaluatedDecisionInput> evaluatedInputs) {
    return fromList(evaluatedInputs, CamundaOperateClientV2::fromEvaluatedInput);
  }

  private static DecisionInstanceInput fromEvaluatedInput(EvaluatedDecisionInput evaluatedInput) {
    DecisionInstanceInput instance = new DecisionInstanceInput();
    instance.setId(evaluatedInput.getInputId());
    instance.setName(evaluatedInput.getInputName());
    instance.setValue(evaluatedInput.getInputValue());
    return instance;
  }

  private static DecisionType fromDecisionDefinitionType(
      DecisionDefinitionType decisionDefinitionType) {
    if (decisionDefinitionType == null) {
      return null;
    }
    return switch (decisionDefinitionType) {
      case UNKNOWN -> DecisionType.UNKNOWN;
      case UNSPECIFIED -> DecisionType.UNSPECIFIED;
      case DECISION_TABLE -> DecisionType.DECISION_TABLE;
      case LITERAL_EXPRESSION -> DecisionType.LITERAL_EXPRESSION;
      case UNKNOWN_ENUM_VALUE ->
          throw new OperateException("Unknown enum value: " + decisionDefinitionType);
    };
  }

  private static DecisionState fromDecisionInstanceState(DecisionInstanceState state) {
    if (state == null) {
      return null;
    }
    return switch (state) {
      case FAILED -> DecisionState.FAILED;
      case UNKNOWN -> DecisionState.UNKNOWN;
      case EVALUATED -> DecisionState.EVALUATED;
      case UNSPECIFIED -> DecisionState.UNSPECIFIED;
      case UNKNOWN_ENUM_VALUE -> throw new OperateException("Unknown enum value: " + state);
    };
  }

  private static DecisionRequirements fromDecisionRequirements(
      io.camunda.client.api.search.response.DecisionRequirements response) {
    DecisionRequirements decisionRequirements = new DecisionRequirements();
    decisionRequirements.setDecisionRequirementsId(response.getDmnDecisionRequirementsId());
    decisionRequirements.setId(response.getDmnDecisionRequirementsId());
    decisionRequirements.setKey(response.getDecisionRequirementsKey());
    decisionRequirements.setName(response.getDmnDecisionRequirementsName());
    decisionRequirements.setResourceName(response.getResourceName());
    decisionRequirements.setTenantId(response.getTenantId());
    decisionRequirements.setVersion(response.getVersion());
    return decisionRequirements;
  }

  private static DecisionDefinition fromDecisionDefinition(
      io.camunda.client.api.search.response.DecisionDefinition decisionDefinition) {
    DecisionDefinition d = new DecisionDefinition();
    d.setId(decisionDefinition.getDmnDecisionId());
    d.setKey(decisionDefinition.getDecisionKey());
    d.setDecisionId(decisionDefinition.getDmnDecisionId());
    d.setName(decisionDefinition.getDmnDecisionName());
    d.setVersion(decisionDefinition.getVersion());
    d.setDecisionRequirementsId(decisionDefinition.getDmnDecisionRequirementsId());
    d.setDecisionRequirementsKey(decisionDefinition.getDecisionRequirementsKey());
    d.setTenantId(decisionDefinition.getTenantId());
    return d;
  }

  private static Incident fromIncident(io.camunda.client.api.search.response.Incident incident) {
    Incident i = new Incident();
    i.setKey(incident.getIncidentKey());
    i.setProcessDefinitionKey(incident.getProcessDefinitionKey());
    i.setProcessInstanceKey(incident.getProcessInstanceKey());
    i.setType(incident.getErrorType().name());
    i.setMessage(incident.getErrorMessage());
    i.setCreationTime(OperateDate.date(incident.getCreationTime()));
    i.setState(incident.getState().name());
    i.setTenantId(incident.getTenantId());
    i.setJobKey(incident.getJobKey());
    return i;
  }

  private static FlowNodeInstance fromElementInstance(ElementInstance elementInstance) {
    FlowNodeInstance flowNodeInstance = new FlowNodeInstance();
    flowNodeInstance.setKey(elementInstance.getElementInstanceKey());
    flowNodeInstance.setProcessInstanceKey(elementInstance.getProcessInstanceKey());
    flowNodeInstance.setProcessDefinitionKey(elementInstance.getProcessDefinitionKey());
    flowNodeInstance.setStartDate(OperateDate.date(elementInstance.getStartDate()));
    flowNodeInstance.setEndDate(OperateDate.date(elementInstance.getEndDate()));
    flowNodeInstance.setFlowNodeId(elementInstance.getElementId());
    flowNodeInstance.setFlowNodeName(elementInstance.getElementName());
    flowNodeInstance.setIncidentKey(elementInstance.getIncidentKey());
    flowNodeInstance.setType(elementInstance.getType().name());
    flowNodeInstance.setState(fromElementInstanceState(elementInstance.getState()));
    flowNodeInstance.setIncident(elementInstance.getIncident());
    flowNodeInstance.setTenantId(elementInstance.getTenantId());
    return flowNodeInstance;
  }

  private static FlowNodeInstanceState fromElementInstanceState(ElementInstanceState state) {
    if (state == null) {
      return null;
    }
    return switch (state) {
      case ACTIVE -> FlowNodeInstanceState.ACTIVE;
      case COMPLETED -> FlowNodeInstanceState.COMPLETED;
      case TERMINATED -> FlowNodeInstanceState.TERMINATED;
      case UNKNOWN_ENUM_VALUE -> throw new OperateException("Unknown enum value: " + state);
    };
  }

  private static List<FlowNodeStatistics> fromProcessElementStatistics(
      List<ProcessElementStatistics> statistics) {
    return fromList(statistics, CamundaOperateClientV2::fromProcessElementStatistic);
  }

  private static FlowNodeStatistics fromProcessElementStatistic(
      ProcessElementStatistics processElementStatistics) {
    FlowNodeStatistics flowNodeStatistics = new FlowNodeStatistics();
    flowNodeStatistics.setActive(processElementStatistics.getActive());
    flowNodeStatistics.setActivityId(processElementStatistics.getElementId());
    flowNodeStatistics.setCanceled(processElementStatistics.getCanceled());
    flowNodeStatistics.setCompleted(processElementStatistics.getCompleted());
    flowNodeStatistics.setIncidents(processElementStatistics.getIncidents());
    return flowNodeStatistics;
  }

  private static List<String> fromSequenceFlows(List<ProcessInstanceSequenceFlow> sequenceFlows) {
    return fromList(sequenceFlows, ProcessInstanceSequenceFlow::getElementId);
  }

  private static ChangeStatus fromCancelProcessInstance(CancelProcessInstanceResponse response) {
    return new ChangeStatus();
  }

  private static Consumer<DecisionDefinitionFilter> fromDecisionDefinitionSearchToFilter(
      SearchQuery query) {
    return fromSearchQueryToFilter(
        query,
        io.camunda.operate.search.DecisionDefinitionFilter.class,
        (filter, decisionDefinitionFilter) -> {
          applyIfNotNull(decisionDefinitionFilter.getId(), filter::decisionDefinitionId);
          applyIfNotNull(decisionDefinitionFilter.getKey(), filter::decisionDefinitionKey);
          applyIfNotNull(decisionDefinitionFilter.getDecisionId(), filter::decisionDefinitionId);
          applyIfNotNull(decisionDefinitionFilter.getName(), filter::name);
          applyIfNotNull(decisionDefinitionFilter.getVersion(), filter::version);
          applyIfNotNull(decisionDefinitionFilter.getTenantId(), filter::tenantId);
          applyIfNotNull(
              decisionDefinitionFilter.getDecisionRequirementsId(), filter::decisionRequirementsId);
          applyIfNotNull(
              decisionDefinitionFilter.getDecisionRequirementsKey(),
              filter::decisionRequirementsKey);
        });
  }

  private static Consumer<DecisionDefinitionSort> fromDecisionDefinitionSearchToSort(
      SearchQuery query) {
    return fromSearchQueryToSort(
        query,
        Map.ofEntries(
            entry("name", DecisionDefinitionSort::name),
            entry("version", DecisionDefinitionSort::version),
            entry("tenantId", DecisionDefinitionSort::tenantId),
            entry("key", DecisionDefinitionSort::decisionDefinitionKey),
            entry("decisionDefinitionKey", DecisionDefinitionSort::decisionDefinitionKey),
            entry("id", DecisionDefinitionSort::decisionDefinitionId),
            entry("decisionDefinitionId", DecisionDefinitionSort::decisionDefinitionId),
            entry("decisionRequirementsKey", DecisionDefinitionSort::decisionRequirementsKey),
            entry("decisionRequirementsId", DecisionDefinitionSort::decisionRequirementsId)));
  }

  private static Consumer<SearchRequestPage> fromSearchQueryToPage(SearchQuery query) {
    return page -> {
      applyIfNotNull(query.getSize(), page::limit);
      applyIfNotNull(query.getAfter(), page::after);
    };
  }

  private static SearchResult<DecisionDefinition> fromDecisionDefinitionSearchResponse(
      SearchResponse<io.camunda.client.api.search.response.DecisionDefinition> response,
      SearchQuery query) {
    return fromSearchResponse(response, query, CamundaOperateClientV2::fromDecisionDefinitions);
  }

  private static List<DecisionDefinition> fromDecisionDefinitions(
      List<io.camunda.client.api.search.response.DecisionDefinition> decisionDefinitions) {
    return fromList(decisionDefinitions, CamundaOperateClientV2::fromDecisionDefinition);
  }

  private static ProcessInstance fromProcessInstance(
      io.camunda.client.api.search.response.ProcessInstance processInstance) {
    ProcessInstance pi = new ProcessInstance();
    pi.setKey(processInstance.getProcessInstanceKey());
    pi.setProcessVersion(processInstance.getProcessDefinitionVersion());
    pi.setBpmnProcessId(processInstance.getProcessDefinitionId());
    pi.setParentKey(processInstance.getParentProcessInstanceKey());
    pi.setParentFlowNodeInstanceKey(processInstance.getParentElementInstanceKey());
    pi.setStartDate(OperateDate.date(processInstance.getStartDate()));
    pi.setEndDate(OperateDate.date(processInstance.getEndDate()));
    pi.setState(fromProcessInstanceState(processInstance.getState()));
    pi.setProcessDefinitionKey(processInstance.getProcessDefinitionKey());
    pi.setTenantId(processInstance.getTenantId());
    return pi;
  }

  private static ProcessInstanceState fromProcessInstanceState(
      io.camunda.client.api.search.enums.ProcessInstanceState state) {
    if (state == null) {
      return null;
    }
    return switch (state) {
      case ACTIVE -> ProcessInstanceState.ACTIVE;
      case COMPLETED -> ProcessInstanceState.COMPLETED;
      case TERMINATED -> ProcessInstanceState.CANCELED;
      case UNKNOWN_ENUM_VALUE -> throw new OperateException("Unknown enum value: " + state);
    };
  }

  private static io.camunda.client.api.search.enums.ProcessInstanceState fromProcessInstanceState(
      ProcessInstanceState state) {
    if (state == null) {
      return null;
    }
    return switch (state) {
      case ACTIVE -> io.camunda.client.api.search.enums.ProcessInstanceState.ACTIVE;
      case COMPLETED -> io.camunda.client.api.search.enums.ProcessInstanceState.COMPLETED;
      case CANCELED -> io.camunda.client.api.search.enums.ProcessInstanceState.TERMINATED;
    };
  }

  private static Consumer<DecisionInstanceFilter> fromDecisionInstanceSearchToFilter(
      SearchQuery query) {
    return fromSearchQueryToFilter(
        query,
        io.camunda.operate.search.DecisionInstanceFilter.class,
        (filter, decisionInstanceFilter) -> {
          applyIfNotNull(decisionInstanceFilter.getId(), filter::decisionInstanceId);
          applyIfNotNull(decisionInstanceFilter.getKey(), filter::decisionInstanceKey);
          applyIfNotNull(decisionInstanceFilter.getDecisionId(), filter::decisionDefinitionId);
          applyIfNotNull(
              decisionInstanceFilter.getState(),
              CamundaOperateClientV2::fromDecisionState,
              filter::state);
          applyIfNotNull(
              decisionInstanceFilter.getEvaluationDate(),
              OperateDate::getOffsetDateTime,
              filter::evaluationDate);
          applyIfNotNull(decisionInstanceFilter.getTenantId(), filter::tenantId);
          applyIfNotNull(decisionInstanceFilter.getEvaluationFailure(), filter::evaluationFailure);
          applyIfNotNull(
              decisionInstanceFilter.getProcessDefinitionKey(), filter::processDefinitionKey);
          applyIfNotNull(
              decisionInstanceFilter.getProcessInstanceKey(), filter::processInstanceKey);
          applyIfNotNull(
              decisionInstanceFilter.getDecisionDefinitionId(), filter::decisionDefinitionId);
          applyIfNotNull(decisionInstanceFilter.getDecisionName(), filter::decisionDefinitionName);
          applyIfNotNull(
              decisionInstanceFilter.getDecisionVersion(), filter::decisionDefinitionVersion);
          applyIfNotNull(
              decisionInstanceFilter.getDecisionType(),
              CamundaOperateClientV2::fromDecisionType,
              filter::decisionDefinitionType);
        });
  }

  private static DecisionInstanceState fromDecisionState(DecisionState decisionState) {
    if (decisionState == null) {
      return null;
    }
    return switch (decisionState) {
      case FAILED -> DecisionInstanceState.FAILED;
      case UNKNOWN -> DecisionInstanceState.UNKNOWN;
      case EVALUATED -> DecisionInstanceState.EVALUATED;
      case UNSPECIFIED -> DecisionInstanceState.UNSPECIFIED;
    };
  }

  private static DecisionDefinitionType fromDecisionType(DecisionType decisionType) {
    if (decisionType == null) {
      return null;
    }
    return switch (decisionType) {
      case UNKNOWN -> DecisionDefinitionType.UNKNOWN;
      case UNSPECIFIED -> DecisionDefinitionType.UNSPECIFIED;
      case DECISION_TABLE -> DecisionDefinitionType.DECISION_TABLE;
      case LITERAL_EXPRESSION -> DecisionDefinitionType.LITERAL_EXPRESSION;
    };
  }

  private static SearchResult<DecisionInstance> fromDecisionInstanceSearchResponse(
      SearchResponse<io.camunda.client.api.search.response.DecisionInstance> execute,
      SearchQuery query) {
    return fromSearchResponse(execute, query, CamundaOperateClientV2::fromDecisionInstances);
  }

  private static List<DecisionInstance> fromDecisionInstances(
      List<io.camunda.client.api.search.response.DecisionInstance> decisionDefinitions) {
    return fromList(decisionDefinitions, CamundaOperateClientV2::fromDecisionInstance);
  }

  private static Consumer<DecisionInstanceSort> fromDecisionInstanceSearchToSort(
      SearchQuery query) {
    return fromSearchQueryToSort(
        query,
        Map.ofEntries(
            entry("key", DecisionInstanceSort::decisionInstanceKey),
            entry("decisionInstanceKey", DecisionInstanceSort::decisionInstanceKey),
            entry("id", DecisionInstanceSort::decisionInstanceId),
            entry("decisionInstanceId", DecisionInstanceSort::decisionInstanceId),
            entry("state", DecisionInstanceSort::state),
            entry("evaluationDate", DecisionInstanceSort::evaluationDate),
            entry("evaluationFailure", DecisionInstanceSort::evaluationFailure),
            entry("processDefinitionKey", DecisionInstanceSort::processDefinitionKey),
            entry("processInstanceKey", DecisionInstanceSort::processInstanceKey),
            entry("elementInstanceKey", DecisionInstanceSort::elementInstanceKey),
            entry("decisionDefinitionKey", DecisionInstanceSort::decisionDefinitionKey),
            entry("decisionDefinitionId", DecisionInstanceSort::decisionDefinitionId),
            entry("decisionDefinitionName", DecisionInstanceSort::decisionDefinitionName),
            entry("version", DecisionInstanceSort::decisionDefinitionVersion),
            entry("decisionDefinitionVersion", DecisionInstanceSort::decisionDefinitionVersion),
            entry("type", DecisionInstanceSort::decisionDefinitionType),
            entry("decisionDefinitionType", DecisionInstanceSort::decisionDefinitionType),
            entry("tenantId", DecisionInstanceSort::tenantId)));
  }

  private static Consumer<ElementInstanceFilter> fromFlownodeInstanceSearchToFilter(
      SearchQuery query) {
    return fromSearchQueryToFilter(
        query,
        FlowNodeInstanceFilter.class,
        (filter, flowNodeInstanceFilter) -> {
          applyIfNotNull(flowNodeInstanceFilter.getKey(), filter::elementInstanceKey);
          applyIfNotNull(
              flowNodeInstanceFilter.getProcessInstanceKey(), filter::processInstanceKey);
          applyIfNotNull(
              flowNodeInstanceFilter.getProcessDefinitionKey(), filter::processDefinitionKey);
          applyIfNotNull(
              flowNodeInstanceFilter.getStartDate(), OperateDate::applyTo, filter::startDate);
          applyIfNotNull(
              flowNodeInstanceFilter.getEndDate(), OperateDate::applyTo, filter::endDate);
          applyIfNotNull(flowNodeInstanceFilter.getFlowNodeId(), filter::elementId);
          applyIfNotNull(flowNodeInstanceFilter.getFlowNodeName(), filter::elementName);
          applyIfNotNull(flowNodeInstanceFilter.getIncidentKey(), filter::incidentKey);
          applyIfNotNull(
              flowNodeInstanceFilter.getType(), ElementInstanceType::valueOf, filter::type);
          applyIfNotNull(
              flowNodeInstanceFilter.getState(),
              CamundaOperateClientV2::fromFlowNodeInstanceState,
              filter::state);
          applyIfNotNull(flowNodeInstanceFilter.getIncident(), filter::hasIncident);
          applyIfNotNull(flowNodeInstanceFilter.getTenantId(), filter::tenantId);
        });
  }

  private static ElementInstanceState fromFlowNodeInstanceState(
      FlowNodeInstanceState flowNodeInstanceState) {
    if (flowNodeInstanceState == null) {
      return null;
    }
    return switch (flowNodeInstanceState) {
      case ACTIVE -> ElementInstanceState.ACTIVE;
      case COMPLETED -> ElementInstanceState.COMPLETED;
      case TERMINATED -> ElementInstanceState.TERMINATED;
    };
  }

  private static SearchResult<FlowNodeInstance> fromElementInstanceSearchResponse(
      SearchResponse<io.camunda.client.api.search.response.ElementInstance> execute,
      SearchQuery query) {
    return fromSearchResponse(execute, query, CamundaOperateClientV2::fromElementInstances);
  }

  private static List<FlowNodeInstance> fromElementInstances(
      List<ElementInstance> elementInstances) {
    return fromList(elementInstances, CamundaOperateClientV2::fromElementInstance);
  }

  private static Consumer<ElementInstanceSort> fromFlowNodeInstanceSearchToSort(SearchQuery query) {
    return fromSearchQueryToSort(
        query,
        Map.ofEntries(
            entry("key", ElementInstanceSort::elementInstanceKey),
            entry("flowNodeInstanceKey", ElementInstanceSort::elementInstanceKey),
            entry("elementInstanceKey", ElementInstanceSort::elementInstanceKey),
            entry("processInstanceKey", ElementInstanceSort::processInstanceKey),
            entry("processDefinitionKey", ElementInstanceSort::processDefinitionKey),
            entry("processDefinitionId", ElementInstanceSort::processDefinitionId),
            entry("startDate", ElementInstanceSort::startDate),
            entry("endDate", ElementInstanceSort::endDate),
            entry("id", ElementInstanceSort::elementId),
            entry("elementId", ElementInstanceSort::elementId),
            entry("flowNodeId", ElementInstanceSort::elementId),
            entry("elementName", ElementInstanceSort::elementName),
            entry("name", ElementInstanceSort::elementName),
            entry("flowNodeName", ElementInstanceSort::elementName),
            entry("type", ElementInstanceSort::type),
            entry("state", ElementInstanceSort::state),
            entry("incidentKey", ElementInstanceSort::incidentKey),
            entry("tenantId", ElementInstanceSort::tenantId)));
  }

  private static Consumer<VariableSort> fromVariableSearchToSort(SearchQuery query) {
    return fromSearchQueryToSort(
        query,
        Map.ofEntries(
            entry("key", VariableSort::variableKey),
            entry("variableKey", VariableSort::variableKey),
            entry("value", VariableSort::value),
            entry("name", VariableSort::name),
            entry("scopeKey", VariableSort::scopeKey),
            entry("processInstanceKey", VariableSort::processInstanceKey),
            entry("tenantId", VariableSort::tenantId)));
  }

  private static Consumer<VariableFilter> fromVariableSearchToFilter(SearchQuery query) {
    return fromSearchQueryToFilter(
        query,
        io.camunda.operate.search.VariableFilter.class,
        (filter, variableFilter) -> {
          applyIfNotNull(variableFilter.getKey(), filter::variableKey);
          applyIfNotNull(variableFilter.getProcessInstanceKey(), filter::processInstanceKey);
          applyIfNotNull(variableFilter.getScopeKey(), filter::scopeKey);
          applyIfNotNull(variableFilter.getName(), filter::name);
          applyIfNotNull(variableFilter.getValue(), filter::value);
          applyIfNotNull(variableFilter.getTruncated(), filter::isTruncated);
          applyIfNotNull(variableFilter.getTenantId(), filter::tenantId);
        });
  }

  private static List<Variable> fromVariables(
      List<io.camunda.client.api.search.response.Variable> variables) {
    return fromList(variables, CamundaOperateClientV2::fromVariable);
  }

  private static SearchResult<Variable> fromVariableSearchResponse(
      SearchResponse<io.camunda.client.api.search.response.Variable> execute, SearchQuery query) {
    return fromSearchResponse(execute, query, CamundaOperateClientV2::fromVariables);
  }

  private static SearchResult<ProcessInstance> fromProcessInstanceSearchResponse(
      SearchResponse<io.camunda.client.api.search.response.ProcessInstance> execute,
      SearchQuery query) {
    return fromSearchResponse(execute, query, CamundaOperateClientV2::fromProcessInstances);
  }

  private static List<ProcessInstance> fromProcessInstances(
      List<io.camunda.client.api.search.response.ProcessInstance> processInstances) {
    return fromList(processInstances, CamundaOperateClientV2::fromProcessInstance);
  }

  private static SearchResult<DecisionRequirements> fromDecisionRequirementsSearchResponse(
      SearchResponse<io.camunda.client.api.search.response.DecisionRequirements> execute,
      SearchQuery query) {
    return fromSearchResponse(execute, query, CamundaOperateClientV2::fromDecisionRequirementsList);
  }

  private static List<DecisionRequirements> fromDecisionRequirementsList(
      List<io.camunda.client.api.search.response.DecisionRequirements> decisionRequirements) {
    return fromList(decisionRequirements, CamundaOperateClientV2::fromDecisionRequirements);
  }

  private static SearchResult<Incident> fromIncidentSearchResponse(
      SearchResponse<io.camunda.client.api.search.response.Incident> execute, SearchQuery query) {
    return fromSearchResponse(execute, query, CamundaOperateClientV2::fromIncidents);
  }

  private static List<Incident> fromIncidents(
      List<io.camunda.client.api.search.response.Incident> incidents) {
    return fromList(incidents, CamundaOperateClientV2::fromIncident);
  }

  private static Consumer<IncidentSort> fromIncidentSearchToSort(SearchQuery query) {
    return fromSearchQueryToSort(
        query,
        Map.ofEntries(
            entry("key", IncidentSort::incidentKey),
            entry("incidentKey", IncidentSort::incidentKey),
            entry("processDefinitionKey", IncidentSort::processDefinitionKey),
            entry("processInstanceKey", IncidentSort::processInstanceKey),
            entry("errorType", IncidentSort::errorType),
            entry("elementId", IncidentSort::elementId),
            entry("flowNodeId", IncidentSort::elementId),
            entry("elementInstanceKey", IncidentSort::elementInstanceKey),
            entry("creationTime", IncidentSort::creationTime),
            entry("state", IncidentSort::state),
            entry("jobKey", IncidentSort::jobKey),
            entry("tenantId", IncidentSort::tenantId)));
  }

  private static Consumer<DecisionRequirementsSort> fromDecisionRequirementsSearchToSort(
      SearchQuery query) {
    return fromSearchQueryToSort(
        query,
        Map.ofEntries(
            entry("decisionRequirementsKey", DecisionRequirementsSort::decisionRequirementsKey),
            entry("key", DecisionRequirementsSort::decisionRequirementsKey),
            entry("decisionRequirementsName", DecisionRequirementsSort::decisionRequirementsName),
            entry("name", DecisionRequirementsSort::decisionRequirementsName),
            entry("version", DecisionRequirementsSort::version),
            entry("id", DecisionRequirementsSort::decisionRequirementsId),
            entry("decisionRequirementsId", DecisionRequirementsSort::decisionRequirementsId),
            entry("tenantId", DecisionRequirementsSort::tenantId)));
  }

  private static Consumer<ProcessInstanceSort> fromProcessInstanceSearchToSort(SearchQuery query) {
    return fromSearchQueryToSort(
        query,
        Map.ofEntries(
            entry("processInstanceKey", ProcessInstanceSort::processInstanceKey),
            entry("key", ProcessInstanceSort::processInstanceKey),
            entry("processDefinitionId", ProcessInstanceSort::processDefinitionId),
            entry("bpmnProcessId", ProcessInstanceSort::processDefinitionId),
            entry("processDefinitionName", ProcessInstanceSort::processDefinitionName),
            entry("processDefinitionVersion", ProcessInstanceSort::processDefinitionVersion),
            entry("processVersion", ProcessInstanceSort::processDefinitionVersion),
            entry("processDefinitionVersionTag", ProcessInstanceSort::processDefinitionVersionTag),
            entry("processDefinitionKey", ProcessInstanceSort::processDefinitionKey),
            entry("parentProcessInstanceKey", ProcessInstanceSort::parentProcessInstanceKey),
            entry("parentKey", ProcessInstanceSort::parentProcessInstanceKey),
            entry("parentFlowNodeInstanceKey", ProcessInstanceSort::parentElementInstanceKey),
            entry("parentElementInstanceKey", ProcessInstanceSort::parentElementInstanceKey),
            entry("startDate", ProcessInstanceSort::startDate),
            entry("endDate", ProcessInstanceSort::endDate),
            entry("state", ProcessInstanceSort::state),
            entry("hasIncident", ProcessInstanceSort::hasIncident),
            entry("tenantId", ProcessInstanceSort::tenantId)));
  }

  private static Consumer<ProcessInstanceFilter> fromProcessInstanceSearchToFilter(
      SearchQuery query) {
    return fromSearchQueryToFilter(
        query,
        io.camunda.operate.search.ProcessInstanceFilter.class,
        (filter, processInstanceFilter) -> {
          applyIfNotNull(processInstanceFilter.getKey(), filter::processInstanceKey);
          applyIfNotNull(
              processInstanceFilter.getProcessVersion(), filter::processDefinitionVersion);
          applyIfNotNull(processInstanceFilter.getBpmnProcessId(), filter::processDefinitionId);
          applyIfNotNull(processInstanceFilter.getParentKey(), filter::parentProcessInstanceKey);
          applyIfNotNull(
              processInstanceFilter.getParentFlowNodeInstanceKey(),
              filter::parentElementInstanceKey);
          applyIfNotNull(
              processInstanceFilter.getStartDate(), OperateDate::applyTo, filter::startDate);
          applyIfNotNull(processInstanceFilter.getEndDate(), OperateDate::applyTo, filter::endDate);
          applyIfNotNull(
              processInstanceFilter.getState(),
              CamundaOperateClientV2::fromProcessInstanceState,
              filter::state);
          applyIfNotNull(
              processInstanceFilter.getProcessDefinitionKey(), filter::processDefinitionKey);
          applyIfNotNull(processInstanceFilter.getTenantId(), filter::tenantId);
        });
  }

  private static Consumer<DecisionRequirementsFilter> fromDecisionRequirementsSearchToFilter(
      SearchQuery query) {
    return fromSearchQueryToFilter(
        query,
        io.camunda.operate.search.DecisionRequirementsFilter.class,
        (filter, decisionRequirementsFilter) -> {
          applyIfNotNull(decisionRequirementsFilter.getKey(), filter::decisionRequirementsKey);
          applyIfNotNull(decisionRequirementsFilter.getName(), filter::decisionRequirementsName);
          applyIfNotNull(decisionRequirementsFilter.getVersion(), filter::version);
          applyIfNotNull(decisionRequirementsFilter.getId(), filter::decisionRequirementsId);
          applyIfNotNull(
              decisionRequirementsFilter.getDecisionRequirementsId(),
              filter::decisionRequirementsId);
          applyIfNotNull(decisionRequirementsFilter.getTenantId(), filter::tenantId);
          applyIfNotNull(decisionRequirementsFilter.getResourceName(), filter::resourceName);
        });
  }

  private static Consumer<IncidentFilter> fromIncidentSearchToFilter(SearchQuery query) {
    return fromSearchQueryToFilter(
        query,
        io.camunda.operate.search.IncidentFilter.class,
        (filter, incidentFilter) -> {
          applyIfNotNull(incidentFilter.getKey(), filter::incidentKey);
          applyIfNotNull(incidentFilter.getProcessDefinitionKey(), filter::processDefinitionKey);
          applyIfNotNull(incidentFilter.getProcessInstanceKey(), filter::processInstanceKey);
          applyIfNotNull(incidentFilter.getType(), IncidentErrorType::valueOf, filter::errorType);
          applyIfNotNull(incidentFilter.getMessage(), filter::errorMessage);
          applyIfNotNull(
              incidentFilter.getCreationTime(),
              OperateDate::getOffsetDateTime,
              filter::creationTime);
          applyIfNotNull(incidentFilter.getState(), IncidentState::valueOf, filter::state);
          applyIfNotNull(incidentFilter.getTenantId(), filter::tenantId);
          applyIfNotNull(incidentFilter.getJobKey(), filter::jobKey);
        });
  }

  @Override
  public ProcessDefinition getProcessDefinition(Long key) throws OperateException {
    return fromProcessDefinition(camundaClient.newProcessDefinitionGetRequest(key).execute());
  }

  @Override
  public String getProcessDefinitionXml(Long key) throws OperateException {
    return camundaClient.newProcessDefinitionGetXmlRequest(key).execute();
  }

  @Override
  public SearchResult<ProcessDefinition> searchProcessDefinitionResults(SearchQuery query)
      throws OperateException {
    return fromProcessDefinitionSearchResponse(
        camundaClient
            .newProcessDefinitionSearchRequest()
            .filter(fromProcessDefinitionSearchToFilter(query))
            .sort(fromProcessDefinitionSearchToSort(query))
            .page(fromSearchQueryToPage(query))
            .execute(),
        query);
  }

  @Override
  public SearchResult<DecisionDefinition> searchDecisionDefinitionResults(SearchQuery query)
      throws OperateException {
    return fromDecisionDefinitionSearchResponse(
        camundaClient
            .newDecisionDefinitionSearchRequest()
            .filter(fromDecisionDefinitionSearchToFilter(query))
            .sort(fromDecisionDefinitionSearchToSort(query))
            .page(fromSearchQueryToPage(query))
            .execute(),
        query);
  }

  @Override
  public SearchResult<DecisionInstance> searchDecisionInstanceResults(SearchQuery query)
      throws OperateException {
    return fromDecisionInstanceSearchResponse(
        camundaClient
            .newDecisionInstanceSearchRequest()
            .filter(fromDecisionInstanceSearchToFilter(query))
            .sort(fromDecisionInstanceSearchToSort(query))
            .page(fromSearchQueryToPage(query))
            .execute(),
        query);
  }

  @Override
  public SearchResult<FlowNodeInstance> searchFlowNodeInstanceResults(SearchQuery query)
      throws OperateException {
    return fromElementInstanceSearchResponse(
        camundaClient
            .newElementInstanceSearchRequest()
            .filter(fromFlownodeInstanceSearchToFilter(query))
            .sort(fromFlowNodeInstanceSearchToSort(query))
            .page(fromSearchQueryToPage(query))
            .execute(),
        query);
  }

  @Override
  public SearchResult<Variable> searchVariableResults(SearchQuery query) throws OperateException {
    return fromVariableSearchResponse(
        camundaClient
            .newVariableSearchRequest()
            .filter(fromVariableSearchToFilter(query))
            .sort(fromVariableSearchToSort(query))
            .page(fromSearchQueryToPage(query))
            .execute(),
        query);
  }

  @Override
  public SearchResult<ProcessInstance> searchProcessInstanceResults(SearchQuery query)
      throws OperateException {
    return fromProcessInstanceSearchResponse(
        camundaClient
            .newProcessInstanceSearchRequest()
            .filter(fromProcessInstanceSearchToFilter(query))
            .sort(fromProcessInstanceSearchToSort(query))
            .page(fromSearchQueryToPage(query))
            .execute(),
        query);
  }

  @Override
  public SearchResult<DecisionRequirements> searchDecisionRequirementsResults(SearchQuery query)
      throws OperateException {
    return fromDecisionRequirementsSearchResponse(
        camundaClient
            .newDecisionRequirementsSearchRequest()
            .filter(fromDecisionRequirementsSearchToFilter(query))
            .sort(fromDecisionRequirementsSearchToSort(query))
            .page(fromSearchQueryToPage(query))
            .execute(),
        query);
  }

  @Override
  public SearchResult<Incident> searchIncidentResults(SearchQuery query) throws OperateException {
    return fromIncidentSearchResponse(
        camundaClient
            .newIncidentSearchRequest()
            .filter(fromIncidentSearchToFilter(query))
            .sort(fromIncidentSearchToSort(query))
            .page(fromSearchQueryToPage(query))
            .execute(),
        query);
  }

  @Override
  public ProcessInstance getProcessInstance(Long key) throws OperateException {
    return fromProcessInstance(camundaClient.newProcessInstanceGetRequest(key).execute());
  }

  @Override
  public ChangeStatus deleteProcessInstance(Long key) throws OperateException {
    return fromCancelProcessInstance(camundaClient.newCancelInstanceCommand(key).execute());
  }

  @Override
  public List<FlowNodeStatistics> getFlowNodeStatistics(Long key) throws OperateException {
    return fromProcessElementStatistics(
        camundaClient.newProcessInstanceElementStatisticsRequest(key).execute());
  }

  @Override
  public List<String> getSequenceFlows(Long key) throws OperateException {
    return fromSequenceFlows(camundaClient.newProcessInstanceSequenceFlowsRequest(key).execute());
  }

  @Override
  public FlowNodeInstance getFlowNodeInstance(Long key) throws OperateException {
    return fromElementInstance(camundaClient.newElementInstanceGetRequest(key).execute());
  }

  @Override
  public Incident getIncident(Long key) throws OperateException {
    return fromIncident(camundaClient.newIncidentGetRequest(key).execute());
  }

  @Override
  public DecisionDefinition getDecisionDefinition(Long key) throws OperateException {
    return fromDecisionDefinition(camundaClient.newDecisionDefinitionGetRequest(key).execute());
  }

  @Override
  public DecisionRequirements getDecisionRequirements(Long key) throws OperateException {
    return fromDecisionRequirements(camundaClient.newDecisionRequirementsGetRequest(key).execute());
  }

  @Override
  public String getDecisionRequirementsXml(Long key) throws OperateException {
    return camundaClient.newDecisionDefinitionGetXmlRequest(key).execute();
  }

  @Override
  public DecisionInstance getDecisionInstance(String id) throws OperateException {
    return fromDecisionInstance(camundaClient.newDecisionInstanceGetRequest(id).execute());
  }

  @Override
  public Variable getVariable(Long key) throws OperateException {
    return fromVariable(camundaClient.newVariableGetRequest(key).execute());
  }
}
