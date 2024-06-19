package io.camunda.operate.auth;

import java.lang.invoke.MethodHandles;
import java.time.LocalDateTime;
import java.util.*;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleAuthentication implements Authentication {

  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private final SimpleCredential simpleCredential;
  private String token;
  private LocalDateTime sessionTimeout;

  public SimpleAuthentication(SimpleCredential simpleCredential) {
    this.simpleCredential = simpleCredential;
  }

  private String retrieveToken() {
    try (CloseableHttpClient client = HttpClients.createDefault()) {
      HttpPost request = buildRequest(simpleCredential);
      String cookie =
          client.execute(
              request,
              response -> {
                Header[] cookieHeaders = response.getHeaders("Set-Cookie");
                String cookieCandidate = null;
                String cookiePrefix = "OPERATE-SESSION";
                for (Header cookieHeader : cookieHeaders) {
                  if (cookieHeader.getValue().startsWith(cookiePrefix)) {
                    cookieCandidate = cookieHeader.getValue();
                    break;
                  }
                }
                return cookieCandidate;
              });
      if (cookie == null) {
        throw new RuntimeException("Unable to authenticate due to missing Set-Cookie");
      }
      token = cookie;
      sessionTimeout = LocalDateTime.now().plus(simpleCredential.sessionTimeout());
    } catch (Exception e) {
      LOG.error("Authenticating for Operate failed due to " + e);
      throw new RuntimeException("Unable to authenticate", e);
    }
    return token;
  }

  private HttpPost buildRequest(SimpleCredential simpleCredential) {
    HttpPost httpPost = new HttpPost(simpleCredential.baseUrl().toString() + "/api/login");
    List<NameValuePair> params = new ArrayList<>();
    params.add(new BasicNameValuePair("username", simpleCredential.username()));
    params.add(new BasicNameValuePair("password", simpleCredential.password()));
    httpPost.setEntity(new UrlEncodedFormEntity(params));
    return httpPost;
  }

  @Override
  public Map.Entry<String, String> getTokenHeader() {
    if (token == null) {
      token = retrieveToken();
    }

    return new AbstractMap.SimpleEntry<>("Cookie", token);
  }

  @Override
  public void resetToken() {
    token = null;
  }
}
