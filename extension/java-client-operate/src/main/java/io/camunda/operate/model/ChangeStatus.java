package io.camunda.operate.model;

public class ChangeStatus {

  @Deprecated private String message;
  @Deprecated private Long deleted;

  @Deprecated
  public String getMessage() {
    return message;
  }

  @Deprecated
  public void setMessage(String message) {
    this.message = message;
  }

  @Deprecated
  public Long getDeleted() {
    return deleted;
  }

  @Deprecated
  public void setDeleted(Long deleted) {
    this.deleted = deleted;
  }
}
