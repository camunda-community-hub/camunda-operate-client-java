package io.camunda.operate.model;

public class DecisionInstanceOutput {

  private String id;
  private String name;
  private String value;
  private String ruleId;
  private Integer ruleIndex;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public String getRuleId() {
    return ruleId;
  }

  public void setRuleId(String ruleId) {
    this.ruleId = ruleId;
  }

  public Integer getRuleIndex() {
    return ruleIndex;
  }

  public void setRuleIndex(Integer ruleIndex) {
    this.ruleIndex = ruleIndex;
  }
}
