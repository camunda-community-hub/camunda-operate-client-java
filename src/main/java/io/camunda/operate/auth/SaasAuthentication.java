package io.camunda.operate.auth;

import java.io.IOException;

import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.message.BasicHeader;

import com.fasterxml.jackson.databind.JsonNode;

import io.camunda.operate.CamundaOperateClient;
import io.camunda.operate.exception.OperateException;
import io.camunda.operate.util.JsonUtils;

public class SaasAuthentication implements AuthInterface {

    private String clientId;
    private String clientSecret;

    public SaasAuthentication(String clientId, String clientSecret) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    @Override
    public void authenticate(CamundaOperateClient client) throws OperateException {

        HttpPost httpPost = new HttpPost("https://login.cloud.camunda.io/oauth/token");
        httpPost.addHeader("Content-Type", "application/json");

        String data = "{\"grant_type\":\"client_credentials\", \"audience\":\"operate.camunda.io\", \"client_id\": \""
                + clientId + "\", \"client_secret\":\"" + clientSecret + "\"}";
        httpPost.setEntity(new StringEntity(data));

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                JsonNode responseBody = JsonUtils.toJsonNode(response.getEntity().getContent());
                String token = responseBody.get("access_token").asText();

                client.setAuthHeader(new BasicHeader("Authorization", "Bearer " + token));
            }
        } catch (IOException e) {
            throw new OperateException(e);
        }
    }
}
