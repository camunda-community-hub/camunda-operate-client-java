package io.camunda.operate.auth;

import static org.assertj.core.api.Assertions.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.camunda.operate.auth.TokenResponseMapper.JacksonTokenResponseMapper;
import org.junit.jupiter.api.Test;

public class TokenResponseTest {
  @Test
  void shouldIgnoreUnknownFields() {
    TokenResponseMapper tokenResponseMapper = new JacksonTokenResponseMapper(new ObjectMapper());
    TokenResponse tokenResponse = tokenResponseMapper.readToken("{\"weird_field\":123}");
    assertThat(tokenResponse).isNotNull();
  }
}
