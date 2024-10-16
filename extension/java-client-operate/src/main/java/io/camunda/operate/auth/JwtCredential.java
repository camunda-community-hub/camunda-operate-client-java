package io.camunda.operate.auth;

import java.net.URL;

public record JwtCredential(
    String clientId, String clientSecret, String audience, URL authUrl, String scope) {}
