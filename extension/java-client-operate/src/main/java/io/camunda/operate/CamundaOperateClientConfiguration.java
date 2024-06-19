package io.camunda.operate;

import io.camunda.operate.auth.Authentication;
import java.net.URL;

public record CamundaOperateClientConfiguration(Authentication authentication, URL baseUrl) {}
