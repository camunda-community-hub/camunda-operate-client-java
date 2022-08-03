package io.camunda.operate.auth;

import java.util.Base64;

import org.apache.hc.core5.http.message.BasicHeader;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.camunda.operate.CamundaOperateClient;
import io.camunda.operate.exception.OperateException;

public abstract class JwtAuthentication implements AuthInterface {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final Base64.Decoder DECODER = Base64.getUrlDecoder();

    public int getExpiration(String token) throws OperateException {
        try {
            String[] chunks = token.split("\\.");
            String payload = new String(DECODER.decode(chunks[1]));
            JsonNode jsonPayload = MAPPER.readValue(payload, JsonNode.class);
            JsonNode exp = jsonPayload.get("exp");
            if (exp==null) {
                return 0;
            } else {
                return exp.asInt();
            }
        } catch (JsonProcessingException e) {
            throw new OperateException("Token is not readable", e);
        }
    }

    public void setToken(CamundaOperateClient client, String token) throws OperateException {
        client.setAuthHeader(new BasicHeader("Authorization", "Bearer " + token));
        client.setTokenExpiration(getExpiration(token));
    }
}
