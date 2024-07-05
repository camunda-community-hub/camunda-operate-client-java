package io.camunda.operate;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.camunda.operate.auth.Authentication;
import java.net.URL;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;

public record CamundaOperateClientConfiguration(
    Authentication authentication,
    URL baseUrl,
    ObjectMapper objectMapper,
    CloseableHttpClient httpClient) {}
