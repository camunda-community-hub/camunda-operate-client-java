package io.camunda.operate.auth;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.camunda.operate.http.TypeReferenceHttpClientResponseHandler;
import io.jsonwebtoken.Jwts;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.apache.hc.core5.util.Asserts;

public class JwtAuthentication implements Authentication {
  private final JwtCredential jwtCredential;
  private final TypeReferenceHttpClientResponseHandler<TokenResponse> responseHandler;
  private String token;
  private LocalDateTime timeout;

  public JwtAuthentication(
      JwtCredential jwtCredential,
      TypeReferenceHttpClientResponseHandler<TokenResponse> responseHandler) {
    this.jwtCredential = jwtCredential;
    this.responseHandler = responseHandler;
  }

  public JwtAuthentication(JwtCredential jwtCredential) {
    this(
        jwtCredential,
        new TypeReferenceHttpClientResponseHandler<>(new TypeReference<>() {}, new ObjectMapper()));
  }

  @Override
  public Map<String, String> getTokenHeader() {
    if (token == null || timeout == null || timeout.isBefore(LocalDateTime.now())) {
      TokenResponse response = retrieveToken();
      token = response.getAccessToken();
      timeout = LocalDateTime.now().plusSeconds(response.getExpiresIn()).minusSeconds(30);
    }
    return Map.of("Authorization", "Bearer " + token);
  }

  @Override
  public void resetToken() {
    this.token = null;
    this.timeout = null;
  }

  private TokenResponse retrieveToken() {
    try (CloseableHttpClient client = HttpClients.createSystem()) {
      HttpPost request = buildRequest();
      TokenResponse tokenResponse = client.execute(request, responseHandler);
      Asserts.notNull(tokenResponse.getAccessToken(), "access_token is null");
      Asserts.notNull(tokenResponse.getExpiresIn(), "expires_in is null");
      return tokenResponse;
    } catch (Exception e) {
      throw new RuntimeException("Failed to retrieve token for Operate authentication", e);
    }
  }

  private HttpPost buildRequest() throws Exception {
    HttpPost httpPost = new HttpPost(jwtCredential.authUrl().toURI());
    httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded");
    List<NameValuePair> formParams = new ArrayList<>();
    formParams.add(new BasicNameValuePair("grant_type", "client_credentials"));
    formParams.add(new BasicNameValuePair("client_id", jwtCredential.clientId()));

    boolean isClientAssertionCertPathProvided =
        jwtCredential.clientAssertionCertPath() != null
            && !jwtCredential.clientAssertionCertPath().isEmpty();

    if (!isClientAssertionCertPathProvided) {
      formParams.add(new BasicNameValuePair("client_secret", jwtCredential.clientSecret()));
    } else {
      formParams.add(
          new BasicNameValuePair(
              "client_assertion",
              createClientAssertion(
                  jwtCredential.clientId(),
                  jwtCredential.authUrl().toString(),
                  jwtCredential.clientAssertionCertPath(),
                  jwtCredential.clientAssertionCertStorePassword())));
    }

    formParams.add(new BasicNameValuePair("audience", jwtCredential.audience()));
    if (jwtCredential.scope() != null && !jwtCredential.scope().isEmpty()) {
      formParams.add(new BasicNameValuePair("scope", jwtCredential.scope()));
    }
    httpPost.setEntity(new UrlEncodedFormEntity(formParams));
    return httpPost;
  }

  /** Create JWT client assertion for OAuth2 authentication */
  private String createClientAssertion(
      String clientId, String issuer, String certPath, String password) throws Exception {
    Instant now = Instant.now();

    var privateKeyData = loadP12Certificate(certPath, password);
    PrivateKey privateKey = privateKeyData.getKey();
    String keyId = privateKeyData.getValue();

    return Jwts.builder()
        .issuer(clientId)
        .subject(clientId)
        .audience()
        .add(issuer)
        .and()
        .issuedAt(Date.from(now))
        .notBefore(Date.from(now))
        .expiration(Date.from(now.plus(5, ChronoUnit.MINUTES)))
        .id(UUID.randomUUID().toString())
        .header()
        .add("alg", "RS256")
        .add("typ", "JWT")
        .add("x5t", keyId)
        .and()
        .signWith(privateKey, Jwts.SIG.RS256)
        .compact();
  }

  private Map.Entry<PrivateKey, String> loadP12Certificate(String certPath, String password)
      throws Exception {
    KeyStore keyStore = KeyStore.getInstance("PKCS12");

    try (FileInputStream fis = new FileInputStream(certPath)) {
      keyStore.load(fis, password != null ? password.toCharArray() : null);
    }

    String alias = keyStore.aliases().nextElement();
    PrivateKey privateKey =
        (PrivateKey) keyStore.getKey(alias, password != null ? password.toCharArray() : null);
    X509Certificate cert = (X509Certificate) keyStore.getCertificate(alias);

    String x5tThumbprint = generateX5tThumbprint(cert);

    return Map.entry(privateKey, x5tThumbprint);
  }

  private String generateX5tThumbprint(X509Certificate certificate) {
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-1");
      byte[] encoded = digest.digest(certificate.getEncoded());
      return Base64.getUrlEncoder().withoutPadding().encodeToString(encoded);
    } catch (Exception e) {
      throw new RuntimeException("Failed to generate x5t thumbprint", e);
    }
  }
}
