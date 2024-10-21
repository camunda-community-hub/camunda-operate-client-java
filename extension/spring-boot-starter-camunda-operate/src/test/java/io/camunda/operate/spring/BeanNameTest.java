package io.camunda.operate.spring;

import static org.assertj.core.api.Assertions.*;

import io.camunda.operate.CamundaOperateClient;
import io.camunda.operate.CamundaOperateClientConfiguration;
import io.camunda.operate.auth.Authentication;
import java.util.stream.Stream;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

@SpringBootTest(properties = "operate.client.profile=simple")
public class BeanNameTest {
  @Autowired ApplicationContext applicationContext;

  @TestFactory
  Stream<DynamicTest> shouldHaveBeanName() {
    return Stream.of(
            applicationContext.getBeanNamesForType(Authentication.class),
            applicationContext.getBeanNamesForType(CamundaOperateClient.class),
            applicationContext.getBeanNamesForType(CloseableHttpClient.class),
            applicationContext.getBeanNamesForType(CamundaOperateClientConfiguration.class))
        .map(s -> DynamicTest.dynamicTest(s[0], () -> testBeanName(s)));
  }

  private void testBeanName(String[] beanNames) {
    assertThat(beanNames).hasSize(1);
    assertThat(beanNames[0]).containsIgnoringCase("operate");
  }
}
