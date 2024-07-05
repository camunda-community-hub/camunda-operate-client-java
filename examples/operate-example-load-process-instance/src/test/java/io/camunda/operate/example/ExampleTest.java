package io.camunda.operate.example;

import io.camunda.operate.CamundaOperateClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ExampleTest {
  @Autowired CamundaOperateClient operateClient;

  @Test
  void shouldRun() {}
}
