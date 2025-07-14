package io.camunda.operate.auth;

import static org.junit.jupiter.api.Assertions.*;

import java.net.URI;
import java.net.URL;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.junit.jupiter.api.Test;

public class JwtAuthenticationTest {

  @Test
  void shouldBuildBasicTokenRequest() throws Exception {
    // Given
    URL authUrl = URI.create("https://auth.example.com/token").toURL();
    JwtCredential credential =
        new JwtCredential(
            "test-client-id",
            "test-client-secret",
            "test-audience",
            authUrl,
            "test-scope",
            null, // no resource parameter - backward compatibility
            null,
            null,
            null,
            null);

    JwtAuthentication authentication = new JwtAuthentication(credential);

    // Use reflection to access private method for testing
    java.lang.reflect.Method buildRequestMethod =
        JwtAuthentication.class.getDeclaredMethod("buildRequest");
    buildRequestMethod.setAccessible(true);

    // When
    HttpPost request = (HttpPost) buildRequestMethod.invoke(authentication);

    // Then
    UrlEncodedFormEntity entity = (UrlEncodedFormEntity) request.getEntity();
    String entityContent = EntityUtils.toString(entity);

    // Verify standard OAuth2 parameters are included
    assertTrue(entityContent.contains("client_id=test-client-id"));
    assertTrue(entityContent.contains("audience=test-audience"));
    assertTrue(entityContent.contains("scope=test-scope"));
    assertTrue(entityContent.contains("grant_type=client_credentials"));
    // Verify resource parameter is not included when null
    assertFalse(entityContent.contains("resource="));
  }

  @Test
  void shouldIncludeResourceParameterInTokenRequest() throws Exception {
    // Given
    URL authUrl = URI.create("https://auth.example.com/token").toURL();
    JwtCredential credential =
        new JwtCredential(
            "test-client-id",
            "test-client-secret",
            "test-audience",
            authUrl,
            "test-scope",
            "test-resource",
            null,
            null,
            null,
            null);

    JwtAuthentication authentication = new JwtAuthentication(credential);

    // Use reflection to access private method for testing
    java.lang.reflect.Method buildRequestMethod =
        JwtAuthentication.class.getDeclaredMethod("buildRequest");
    buildRequestMethod.setAccessible(true);

    // When
    HttpPost request = (HttpPost) buildRequestMethod.invoke(authentication);

    // Then
    UrlEncodedFormEntity entity = (UrlEncodedFormEntity) request.getEntity();
    String entityContent = EntityUtils.toString(entity);

    assertTrue(entityContent.contains("resource=test-resource"));
    assertTrue(entityContent.contains("client_id=test-client-id"));
    assertTrue(entityContent.contains("audience=test-audience"));
    assertTrue(entityContent.contains("scope=test-scope"));
    assertTrue(entityContent.contains("grant_type=client_credentials"));
  }

  @Test
  void shouldNotIncludeResourceParameterWhenNull() throws Exception {
    // Given
    URL authUrl = URI.create("https://auth.example.com/token").toURL();
    JwtCredential credential =
        new JwtCredential(
            "test-client-id",
            "test-client-secret",
            "test-audience",
            authUrl,
            "test-scope",
            null, // resource is null
            null,
            null,
            null,
            null);

    JwtAuthentication authentication = new JwtAuthentication(credential);

    // Use reflection to access private method for testing
    java.lang.reflect.Method buildRequestMethod =
        JwtAuthentication.class.getDeclaredMethod("buildRequest");
    buildRequestMethod.setAccessible(true);

    // When
    HttpPost request = (HttpPost) buildRequestMethod.invoke(authentication);

    // Then
    UrlEncodedFormEntity entity = (UrlEncodedFormEntity) request.getEntity();
    String entityContent = EntityUtils.toString(entity);

    assertFalse(entityContent.contains("resource="));
    assertTrue(entityContent.contains("client_id=test-client-id"));
    assertTrue(entityContent.contains("audience=test-audience"));
    assertTrue(entityContent.contains("scope=test-scope"));
  }

  @Test
  void shouldNotIncludeResourceParameterWhenEmpty() throws Exception {
    // Given
    URL authUrl = URI.create("https://auth.example.com/token").toURL();
    JwtCredential credential =
        new JwtCredential(
            "test-client-id",
            "test-client-secret",
            "test-audience",
            authUrl,
            "test-scope",
            "", // resource is empty
            null,
            null,
            null,
            null);

    JwtAuthentication authentication = new JwtAuthentication(credential);

    // Use reflection to access private method for testing
    java.lang.reflect.Method buildRequestMethod =
        JwtAuthentication.class.getDeclaredMethod("buildRequest");
    buildRequestMethod.setAccessible(true);

    // When
    HttpPost request = (HttpPost) buildRequestMethod.invoke(authentication);

    // Then
    UrlEncodedFormEntity entity = (UrlEncodedFormEntity) request.getEntity();
    String entityContent = EntityUtils.toString(entity);

    assertFalse(entityContent.contains("resource="));
    assertTrue(entityContent.contains("client_id=test-client-id"));
    assertTrue(entityContent.contains("audience=test-audience"));
    assertTrue(entityContent.contains("scope=test-scope"));
  }
}
