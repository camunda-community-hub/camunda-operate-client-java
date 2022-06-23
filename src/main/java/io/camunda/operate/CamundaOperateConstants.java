package io.camunda.operate;

import java.util.AbstractMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.camunda.operate.dto.FlownodeInstance;
import io.camunda.operate.dto.Incident;
import io.camunda.operate.dto.ProcessDefinition;
import io.camunda.operate.dto.ProcessInstance;
import io.camunda.operate.dto.Variable;

public class CamundaOperateConstants {

    public static Map<Class<?>, String> OBJECT_APIS = toMap(ProcessDefinition.class, "/v1/process-definitions", 
                ProcessInstance.class, "/v1/process-instances",
                FlownodeInstance.class, "/v1/flownode-instances",
                Incident.class, "/v1/incidents",
                Variable.class, "/v1/variables");
    
    private static Map<Class<?>, String> toMap(Object... array) {
        AbstractMap.SimpleEntry<Class<?>, String>[] entryArray = new AbstractMap.SimpleEntry[array.length/2];
        for(int i=0;i<entryArray.length;i++) {
            entryArray[i]= new  AbstractMap.SimpleEntry<Class<?>, String>((Class<?>)array[i*2], (String) array[i*2+1]);
        }
        return Stream.of(entryArray)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
