package io.camunda.operate.spring;

import io.camunda.operate.spring.OperateClientConfigurationProperties.Profile;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.boot.logging.DeferredLogFactory;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.ClassPathResource;

public class OperatePropertiesPostProcessor implements EnvironmentPostProcessor {
  private final Log log;

  public OperatePropertiesPostProcessor(DeferredLogFactory deferredLogFactory) {
    log = deferredLogFactory.getLog(OperatePropertiesPostProcessor.class);
  }

  @Override
  public void postProcessEnvironment(
      ConfigurableEnvironment environment, SpringApplication application) {
    try {
      Profile profile = environment.getProperty("operate.client.profile", Profile.class);
      if (profile == null) {
        profile = detectProfile(environment);
        if (profile == null) {
          return;
        }
      }
      YamlPropertySourceLoader loader = new YamlPropertySourceLoader();
      String propertiesFile = "operate-profiles/" + determinePropertiesFile(profile);
      ClassPathResource resource = new ClassPathResource(propertiesFile);
      List<PropertySource<?>> props = loader.load(propertiesFile, resource);
      for (PropertySource<?> prop : props) {
        environment.getPropertySources().addLast(prop); // lowest priority
      }
    } catch (Exception e) {
      throw new IllegalStateException("Error while post processing camunda properties", e);
    }
  }

  private String determinePropertiesFile(Profile clientMode) {
    switch (clientMode) {
      case oidc -> {
        return "oidc.yaml";
      }
      case saas -> {
        return "saas.yaml";
      }
      case simple -> {
        return "simple.yaml";
      }
      case v2 -> {
        return "v2.yaml";
      }
    }
    throw new IllegalStateException("Unknown client mode " + clientMode);
  }

  private Profile detectProfile(ConfigurableEnvironment environment) {
    // cluster id is set -> always saas
    if (environment.getProperty("operate.client.cluster-id") != null) {
      log.debug("Detected 'operate.client.profile'='saas' based on 'operate.client.cluster-id'");
      return Profile.saas;
    }
    // here, we can try to distinguish between simple and oidc
    Set<Profile> potentialProfiles = new HashSet<>();
    if (environment.getProperty("operate.client.username") != null) {
      log.debug("Detected 'operate.client.profile'='simple' based on 'operate.client.username'");
      potentialProfiles.add(Profile.simple);
    }
    if (environment.getProperty("operate.client.password") != null) {
      log.debug("Detected 'operate.client.profile'='simple' based on 'operate.client.password'");
      potentialProfiles.add(Profile.simple);
    }
    if (environment.getProperty("operate.client.client-id") != null) {
      log.debug("Detected 'operate.client.profile'='oidc' based on 'operate.client.client-id'");
      potentialProfiles.add(Profile.oidc);
    }
    if (environment.getProperty("operate.client.client-secret") != null) {
      log.debug("Detected 'operate.client.profile'='oidc' based on 'operate.client.client-secret'");
      potentialProfiles.add(Profile.oidc);
    }
    if (potentialProfiles.isEmpty()) {
      log.debug("No 'operate.client.profile' could be detected");
      return null;
    }
    if (potentialProfiles.size() > 1) {
      log.warn(
          "Multiple implicit values for 'operate.client.profile' detected: " + potentialProfiles);
    }
    return potentialProfiles.iterator().next();
  }
}
