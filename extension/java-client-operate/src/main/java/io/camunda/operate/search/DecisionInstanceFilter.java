package io.camunda.operate.search;

import io.camunda.operate.model.DecisionInstance;
import io.camunda.operate.model.DecisionInstanceInput;
import io.camunda.operate.model.DecisionInstanceOutput;
import java.util.List;

public class DecisionInstanceFilter extends DecisionInstance implements Filter {

  public static DecisionInstanceFilterBuilder builder() {
    return new DecisionInstanceFilterBuilder();
  }

  @Override
  @Deprecated
  public void setResult(String result) {
    super.setResult(result);
  }

  @Override
  @Deprecated
  public String getResult() {
    return super.getResult();
  }

  @Override
  @Deprecated
  public List<DecisionInstanceInput> getEvaluatedInputs() {
    return super.getEvaluatedInputs();
  }

  @Override
  @Deprecated
  public void setEvaluatedInputs(List<DecisionInstanceInput> evaluatedInputs) {
    super.setEvaluatedInputs(evaluatedInputs);
  }

  @Override
  @Deprecated
  public List<DecisionInstanceOutput> getEvaluatedOutputs() {
    return super.getEvaluatedOutputs();
  }

  @Override
  @Deprecated
  public void setEvaluatedOutputs(List<DecisionInstanceOutput> evaluatedOutputs) {
    super.setEvaluatedOutputs(evaluatedOutputs);
  }
}
