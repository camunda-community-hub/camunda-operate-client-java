package io.camunda.common.auth;

@Deprecated
public class JwtConfig {
  private JwtCredential jwtCredential;

  public void addProduct(Product product, JwtCredential credential) {
    this.jwtCredential = credential;
  }

  public JwtCredential getJwtCredential() {
    return jwtCredential;
  }

  public JwtCredential getProduct(Product product) {
    return jwtCredential;
  }
}
