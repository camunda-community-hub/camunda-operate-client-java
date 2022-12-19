package io.camunda.operate.auth;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.StringEntity;

import com.fasterxml.jackson.databind.JsonNode;

import io.camunda.operate.CamundaOperateClient;
import io.camunda.operate.exception.OperateException;
import io.camunda.operate.util.JsonUtils;
import org.apache.hc.core5.http.message.BasicNameValuePair;

public class SaasAuthentication extends JwtAuthentication {

    private final String clientId;
    private final String clientSecret;

    private final String baseUrl;
    private final String authUrl;
    private final String authRequestEncoding;

    public SaasAuthentication(String clientId, String clientSecret) {
        this("https://login.cloud.camunda.io/oauth/token", "operate.camunda.io",
                clientId, clientSecret, ContentType.APPLICATION_JSON.getMimeType());
    }

    public SaasAuthentication(String authUrl, String baseUrl, String clientId, String clientSecret) {
        this(authUrl, baseUrl, clientId, clientSecret, ContentType.APPLICATION_JSON.getMimeType());
    }

    public SaasAuthentication(
            final String authUrl,
            final String baseUrl,
            final String clientId,
            final String clientSecret,
            final String authRequestEncoding) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.baseUrl = baseUrl;
        this.authUrl = authUrl;
        this.authRequestEncoding = authRequestEncoding;
    }

    @Override
    public void authenticate(CamundaOperateClient client) throws OperateException {

        HttpPost httpPost = new HttpPost(authUrl);

        if (ContentType.APPLICATION_JSON.getMimeType().equals(authRequestEncoding)) {
            httpPost.addHeader("Content-Type", ContentType.APPLICATION_JSON.getMimeType());
            String data = "{\"grant_type\":\"client_credentials\", \"audience\":\"" + baseUrl + "\", \"client_id\": \""
                    + clientId + "\", \"client_secret\":\"" + clientSecret + "\"}";
            httpPost.setEntity(new StringEntity(data));
        } else if (ContentType.APPLICATION_FORM_URLENCODED.getMimeType().equals(authRequestEncoding)) {
            httpPost.addHeader("Content-Type", ContentType.APPLICATION_FORM_URLENCODED.getMimeType());
            httpPost.setEntity(new UrlEncodedFormEntity(Arrays.asList(
                    new BasicNameValuePair("grant_type", "client_credentials"),
                    new BasicNameValuePair("audience", baseUrl),
                    new BasicNameValuePair("client_id", clientId),
                    new BasicNameValuePair("client_secret", clientSecret)
            )));
        } else {
            throw new OperateException("The " + authRequestEncoding + " auth request content type is not yet supported");
        }

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                if (response.getCode() > 399) {
                  throw new OperateException("Authentication error : "+response.getCode()+" "+response.getReasonPhrase());
                }
                JsonNode responseBody = JsonUtils.toJsonNode(response.getEntity().getContent());
                String token = responseBody.get("access_token").asText();

                setToken(client, token);
            }
        } catch (IOException e) {
            throw new OperateException(e);
        }
    }
}
