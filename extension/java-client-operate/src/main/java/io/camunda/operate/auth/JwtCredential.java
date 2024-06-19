package io.camunda.operate.auth;

public record JwtCredential(
    String clientId, String clientSecret, String audience, String authUrl) {}
