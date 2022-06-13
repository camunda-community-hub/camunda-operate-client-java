package io.camunda.operate.auth;

import io.camunda.operate.CamundaOperateClient;
import io.camunda.operate.exception.OperateException;

public interface AuthInterface {
    public void authenticate(CamundaOperateClient client) throws OperateException;
}
