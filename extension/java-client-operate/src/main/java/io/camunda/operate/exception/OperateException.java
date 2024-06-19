package io.camunda.operate.exception;

public class OperateException extends Exception {

  public OperateException() {
    super();
  }

  public OperateException(Exception e) {
    super(e);
  }

  public OperateException(String message) {
    super(message);
  }

  public OperateException(String message, Exception e) {
    super(message, e);
  }
}
