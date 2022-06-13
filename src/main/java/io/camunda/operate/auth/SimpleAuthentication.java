package io.camunda.operate.auth;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.ProtocolException;
import org.apache.hc.core5.http.message.BasicHeader;
import org.apache.hc.core5.http.message.BasicNameValuePair;

import io.camunda.operate.CamundaOperateClient;
import io.camunda.operate.exception.OperateException;

/**
 * To use if Operate is not configured with Identity and Keycloak
 */
public class SimpleAuthentication implements AuthInterface {

    private String operateUsername;

    private String operatePassword;

    private String operateUrl;

    public SimpleAuthentication(String operateUsername, String operatePassword, String operateUrl) {
        this.operateUsername = operateUsername;
        this.operatePassword = operatePassword;
        this.operateUrl = operateUrl;
    }

    @Override
    public void authenticate(CamundaOperateClient client) throws OperateException {

        HttpPost httpPost = new HttpPost(operateUrl + "/api/login");

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("username", operateUsername));
        params.add(new BasicNameValuePair("password", operatePassword));
        httpPost.setEntity(new UrlEncodedFormEntity(params));

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                String cookie = response.getHeader("Set-Cookie").getValue();

                client.setAuthHeader(new BasicHeader("Cookie", cookie));
            }
        } catch (IOException | ProtocolException e) {
            throw new OperateException(e);
        }
    }
}
