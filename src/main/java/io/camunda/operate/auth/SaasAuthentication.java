package io.camunda.operate.auth;

import java.io.IOException;

import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.StringEntity;

import com.fasterxml.jackson.databind.JsonNode;

import io.camunda.operate.CamundaOperateClient;
import io.camunda.operate.exception.OperateException;
import io.camunda.operate.util.JsonUtils;

public class SaasAuthentication extends JwtAuthentication {

    private String clientId;
    private String clientSecret;

    private String baseUrl;
    private String authUrl;

    public SaasAuthentication(String clientId, String clientSecret) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;

        this.authUrl = "https://login.cloud.camunda.io/oauth/token";
        this.baseUrl = "operate.camunda.io";
    }

    public SaasAuthentication(String authUrl, String baseUrl, String clientId, String clientSecret) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.baseUrl = baseUrl;
        this.authUrl = authUrl;
    }

    @Override
    public void authenticate(CamundaOperateClient client) throws OperateException {

        HttpPost httpPost = new HttpPost(authUrl);
        httpPost.addHeader("Content-Type", "application/json");

        String data = "{\"grant_type\":\"client_credentials\", \"audience\":\"" + baseUrl + "\", \"client_id\": \""
                + clientId + "\", \"client_secret\":\"" + clientSecret + "\"}";
        httpPost.setEntity(new StringEntity(data));

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                JsonNode responseBody = JsonUtils.toJsonNode(response.getEntity().getContent());
                String token = responseBody.get("access_token").asText();

                setToken(client, token);
            }
        } catch (IOException e) {
            throw new OperateException(e);
        }
    }
}
