package io.camunda.operate.auth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public interface TokenResponseParser {
  TokenResponse parse(String response);

  public class JacksonTokenResponseParser implements TokenResponseParser {
    private final ObjectMapper objectMapper;

    public JacksonTokenResponseParser(ObjectMapper objectMapper) {
      this.objectMapper = objectMapper;
    }

    @Override
    public TokenResponse parse(String response) {
      try {
        return objectMapper.readValue(response, TokenResponse.class);
      } catch (JsonProcessingException e) {
        throw new RuntimeException("Error while parsing token response", e);
      }
    }
  }
}
