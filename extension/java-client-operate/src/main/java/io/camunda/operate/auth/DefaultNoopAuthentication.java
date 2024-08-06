package io.camunda.operate.auth;

import java.lang.invoke.MethodHandles;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default implementation for Authentication Typically you will replace this by a proper
 * authentication by setting the right properties
 */
public class DefaultNoopAuthentication implements Authentication {

  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private final String errorMessage =
      "Unable to determine authentication. Please check your configuration";

  public DefaultNoopAuthentication() {
    LOG.error(errorMessage);
  }

  @Override
  public void resetToken() {
    throw new UnsupportedOperationException(errorMessage);
  }

  @Override
  public Map<String, String> getTokenHeader() {
    throw new UnsupportedOperationException("Unable to determine authentication");
  }
}
