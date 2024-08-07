package io.camunda.operate.example;

import io.camunda.operate.CamundaOperateClient;
import io.camunda.operate.exception.OperateException;
import io.camunda.operate.model.ChangeStatus;
import io.camunda.operate.model.FlowNodeInstance;
import io.camunda.operate.model.ProcessInstance;
import io.camunda.operate.model.Variable;
import io.camunda.operate.search.FlowNodeInstanceFilter;
import io.camunda.operate.search.SearchQuery;
import io.camunda.operate.search.VariableFilter;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/process-instances")
public class ProcessInstanceController {
  private final CamundaOperateClient camundaOperateClient;

  @Autowired
  public ProcessInstanceController(CamundaOperateClient camundaOperateClient) {
    this.camundaOperateClient = camundaOperateClient;
  }

  @GetMapping
  public List<ProcessInstanceWrapper> getProcessInstances() throws OperateException {
    return camundaOperateClient.searchProcessInstances(SearchQuery.builder().build()).stream()
        .map(
            pi -> {
              try {
                return new ProcessInstanceWrapper(
                    pi,
                    camundaOperateClient.searchFlowNodeInstances(
                        SearchQuery.builder()
                            .filter(
                                FlowNodeInstanceFilter.builder()
                                    .processInstanceKey(pi.getKey())
                                    .build())
                            .build()),
                    camundaOperateClient.searchVariables(
                        SearchQuery.builder()
                            .filter(
                                VariableFilter.builder().processInstanceKey(pi.getKey()).build())
                            .build()));
              } catch (OperateException e) {
                throw new RuntimeException(e);
              }
            })
        .toList();
  }

  @DeleteMapping("/{key}")
  public ChangeStatus deleteProcessInstance(@PathVariable(name = "key") Long key)
      throws OperateException {
    return camundaOperateClient.deleteProcessInstance(key);
  }

  public record ProcessInstanceWrapper(
      ProcessInstance processInstance,
      List<FlowNodeInstance> flowNodeInstances,
      List<Variable> variables) {}
}
