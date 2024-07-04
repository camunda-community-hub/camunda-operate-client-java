package io.camunda.operate.http;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.core.type.TypeReference;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class TypeReferenceTest {

  @Test
  void shouldFindTypeReference() {
    TypeReference<String> stringTypeReference1 = new TypeReference<>() {};
    TypeReference<String> stringTypeReference2 = new TypeReference<>() {};
    Map<TypeReference<?>, String> map =
        Map.of(stringTypeReference1, "/1", stringTypeReference2, "/2");
    String one = map.get(stringTypeReference1);
    String two = map.get(stringTypeReference2);
    assertEquals(one, "/1");
    assertEquals(two, "/2");
  }
}
