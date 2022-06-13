package io.camunda.operate;

import java.util.Map;

import io.camunda.operate.dto.FlownodeInstance;
import io.camunda.operate.dto.Incident;
import io.camunda.operate.dto.ProcessDefinition;
import io.camunda.operate.dto.ProcessInstance;
import io.camunda.operate.dto.Variable;

public class CamundaOperateConstants {

    public static Map<Class<?>, String> OBJECT_APIS = Map.of(ProcessDefinition.class, "/v1/process-definitions", 
                ProcessInstance.class, "/v1/process-instances",
                FlownodeInstance.class, "/v1/flownode-instances",
                Incident.class, "/v1/incidents",
                Variable.class, "/v1/variables");
    
}
