package io.camunda.operate.http;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.camunda.operate.auth.Authentication;
import java.io.IOException;
import java.net.URI;
import java.util.*;
import org.apache.hc.client5.http.classic.methods.HttpDelete;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.internal.matchers.apachecommons.ReflectionEquals;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class DefaultHttpClientTest {

  @Mock Authentication authentication;
  @Mock CloseableHttpClient chClient;
  ObjectMapper jsonMapper = new ObjectMapper();

  @Test
  public void shouldReturnGetType() throws IOException {
    // given
    TypeReference<MyResponseClass> typeReference = new TypeReference<>() {};
    TypeReference<MyResponseClass> typeReferenceWithSameType = new TypeReference<>() {};

    Map<TypeReference<?>, String> productMap = new HashMap<>();
    productMap.put(typeReference, "/request/{some}");
    productMap.put(typeReferenceWithSameType, "/no");
    DefaultHttpClient defaultHttpClient =
        new DefaultHttpClient(
            URI.create("http://localhost:8081").toURL(),
            authentication,
            chClient,
            jsonMapper,
            productMap);
    MyResponseClass expectedOutput = new MyResponseClass();
    expectedOutput.setName("test-name");
    ArgumentCaptor<HttpGet> captor = ArgumentCaptor.forClass(HttpGet.class);
    // when
    when(chClient.execute(captor.capture(), any(HttpClientResponseHandler.class)))
        .thenReturn(expectedOutput);
    when(authentication.getTokenHeader()).thenReturn(Map.of("key", "value"));

    MyResponseClass parsedResponse =
        defaultHttpClient.get(typeReference, Map.of("some", "test-name"));

    // then
    assertTrue(new ReflectionEquals(expectedOutput).matches(parsedResponse));
    assertEquals("/request/test-name", captor.getValue().getRequestUri());
  }

  @Test
  public void shouldReturnPostType() throws IOException {
    // given
    Map<TypeReference<?>, String> productMap = new HashMap<>();
    DefaultHttpClient defaultHttpClient =
        new DefaultHttpClient(
            URI.create("http://localhost:8081").toURL(),
            authentication,
            chClient,
            jsonMapper,
            productMap);
    MyResponseClass insideClass = new MyResponseClass();
    insideClass.setName("test-name");
    List<MyResponseClass> expectedOutput = new ArrayList<>();
    expectedOutput.add(insideClass);

    // when
    when(chClient.execute(any(HttpPost.class), any(HttpClientResponseHandler.class)))
        .thenReturn(expectedOutput);
    when(authentication.getTokenHeader()).thenReturn(Map.of("key", "value"));
    List<MyResponseClass> parsedResponse =
        defaultHttpClient.post(new TypeReference<>() {}, Map.of());

    // then
    assertEquals(expectedOutput.size(), parsedResponse.size());
    assertTrue(new ReflectionEquals(expectedOutput.get(0)).matches(parsedResponse.get(0)));
  }

  @Test
  public void shouldReturnDeleteType() throws IOException {
    // given
    Map<TypeReference<?>, String> productMap = new HashMap<>();
    DefaultHttpClient defaultHttpClient =
        new DefaultHttpClient(
            URI.create("http://localhost:8081").toURL(),
            authentication,
            chClient,
            jsonMapper,
            productMap);
    MyResponseClass expectedOutput = new MyResponseClass();
    expectedOutput.setName("test-name");

    // when
    when(chClient.execute(any(HttpDelete.class), any(HttpClientResponseHandler.class)))
        .thenReturn(expectedOutput);
    when(authentication.getTokenHeader()).thenReturn(Map.of("key", "value"));
    MyResponseClass parsedResponse = defaultHttpClient.delete(new TypeReference<>() {}, Map.of());

    // then
    assertTrue(new ReflectionEquals(expectedOutput).matches(parsedResponse));
  }

  // TODO: add more tests for retrieving the right url path based on input

  public static class MyResponseClass {
    private String name;

    public void setName(String name) {
      this.name = name;
    }
  }

  public static class MySelectorClass {}
}
