package io.camunda.operate.http;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.camunda.operate.auth.Authentication;
import io.camunda.operate.exception.SdkException;
import java.lang.invoke.MethodHandles;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.hc.client5.http.classic.methods.HttpDelete;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.message.BasicHeader;

/** Default Http Client powered by Apache HttpClient */
public class DefaultHttpClient implements HttpClient {
  private final Map<TypeReference<?>, String> endpointMap;
  private final CloseableHttpClient httpClient;
  private final Authentication authentication;
  private final ObjectMapper objectMapper;
  private final String baseUrl;

  public DefaultHttpClient(
      URL baseUrl,
      Authentication authentication,
      CloseableHttpClient httpClient,
      ObjectMapper objectMapper,
      Map<TypeReference<?>, String> endpointMap) {
    this.authentication = authentication;
    this.httpClient = httpClient;
    this.objectMapper = objectMapper.copy();
    this.endpointMap = endpointMap;
    this.objectMapper
        .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .setSerializationInclusion(JsonInclude.Include.NON_NULL);
    this.baseUrl = baseUrl.toString();
  }

  @Override
  public <T> T get(TypeReference<T> responseType, Map<String, String> pathParams) {
    String url = baseUrl + retrievePath(responseType, pathParams);
    HttpGet httpGet = new HttpGet(url);
    retrieveToken().forEach(httpGet::addHeader);
    try {
      return httpClient.execute(httpGet, handleResponse(responseType));
    } catch (Exception e) {
      throw new SdkException(String.format("Failed GET to %s, due to %s", url, e.getMessage()), e);
    }
  }

  @Override
  public <T, U> T post(TypeReference<T> responseType, U body) {

    String url = baseUrl + retrievePath(responseType, Map.of());
    HttpPost httpPost = new HttpPost(url);
    httpPost.addHeader("Content-Type", "application/json");
    retrieveToken().forEach(httpPost::addHeader);
    String data;
    try {
      data = objectMapper.writeValueAsString(body);
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Error while parsing " + body + "of type " + body.getClass(), e);
    }
    httpPost.setEntity(new StringEntity(data));
    try {
      return httpClient.execute(httpPost, handleResponse(responseType));
    } catch (Exception e) {
      throw new SdkException(
          String.format("Failed POST to %s with body %s, due to %s", url, data, e.getMessage()), e);
    }
  }

  @Override
  public <T> T delete(TypeReference<T> responseType, Map<String, String> pathParams) {
    String resourcePath = retrievePath(responseType, pathParams);
    String url = baseUrl + resourcePath;
    HttpDelete httpDelete = new HttpDelete(url);
    retrieveToken().forEach(httpDelete::addHeader);
    try {
      return httpClient.execute(httpDelete, handleResponse(responseType));
    } catch (Exception e) {
      throw new SdkException(
          String.format("Failed DELETE to %s, due to %s", url, e.getMessage()), e);
    }
  }

  private <T> String retrievePath(TypeReference<T> clazz, Map<String, String> pathParams) {
    AtomicReference<String> path = new AtomicReference<>();
    if (endpointMap.containsKey(clazz)) {
      path.set(endpointMap.get(clazz));
    }
    for (String pathParam : pathParams.keySet()) {
      String pathParamMarker = "{" + pathParam + "}";
      if (path.get().contains(pathParamMarker)) {
        path.set(path.get().replace(pathParamMarker, pathParams.get(pathParam)));
      }
    }
    return path.get();
  }

  private List<? extends Header> retrieveToken() {
    Map<String, String> header = authentication.getTokenHeader();
    return header.entrySet().stream().map(e -> new BasicHeader(e.getKey(), e.getValue())).toList();
  }

  private <T> HttpClientResponseHandler<T> handleResponse(TypeReference<T> responseType) {
    return new TypeReferenceHttpClientResponseHandler<>(
        responseType, objectMapper, this::handleErrorResponse);
  }

  private SdkException handleErrorResponse(Integer code) {
    if (code == HttpStatus.SC_UNAUTHORIZED || code == HttpStatus.SC_FORBIDDEN) {
      authentication.resetToken();
    }
    return new SdkException("Response not successful: " + code);
  }
}
