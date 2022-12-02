[![Community Extension](https://img.shields.io/badge/Community%20Extension-An%20open%20source%20community%20maintained%20project-FF4700)](https://github.com/camunda-community-hub/community)
![Compatible with: Camunda Platform 8](https://img.shields.io/badge/Compatible%20with-Camunda%20Platform%208-0072Ce)
[![](https://img.shields.io/badge/Lifecycle-Incubating-blue)](https://github.com/Camunda-Community-Hub/community/blob/main/extension-lifecycle.md#incubating-)

# Camunda Operate Client

This project is designed to simplify communication between a Java backend and the [Operate API of Camunda Platform 8](https://docs.camunda.io/docs/apis-clients/operate-api/). This project is still a draft.

# How to use the client

Simply build a CamundaOperateClient that takes an authentication and the Operate URL as parameters.

```java
SimpleAuthentication sa = new SimpleAuthentication("demo", "demo", "http://localhost:8081");
CamundaOperateClient client = new CamundaOperateClient.Builder().operateUrl("http://localhost:8081").authentication(sa).build();

//search process definitions based on filters
ProcessDefinitionFilter processDefinitionFilter = new ProcessDefinitionFilter.Builder().name("Customer Onboarding").build();
SearchQuery procDefQuery = new SearchQuery.Builder().withFilter(processDefinitionFilter).withSize(20).withSort(new Sort("version", SortOrder.ASC)).build();

List<ProcessDefinition> processDefinitions = client.searchProcessDefinitions(procDefQuery);

//get a process definition by its key
ProcessDefinition def = client.getProcessDefinition(1L);

//search process instances based on filters
ProcessInstanceFilter instanceFilter = new ProcessInstanceFilter.Builder().bpmnProcessId("customer_onboarding_en").startDate(new DateFilter(new Date(), DateFilterRange.MONTH)).build();
SearchQuery instanceQuery = new SearchQuery.Builder().withFilter(instanceFilter).withSize(20).withSort(new Sort("state", SortOrder.ASC)).build();

List<ProcessInstance> instances = client.searchProcessInstances(instanceQuery);
       
//get a process instance by its key
ProcessInstance instance = client.getProcessInstance(instances.get(0).getKey());

//search flownode instances based on filters
FlownodeInstanceFilter flownodeFilter = new FlownodeInstanceFilter.Builder()
.processInstanceKey(4L).startDate(new DateFilter(new Date(), DateFilterRange.YEAR)).build();
SearchQuery flownodeQuery = new SearchQuery.Builder().withFilter(flownodeFilter).withSize(20).withSort(new Sort("state", SortOrder.ASC)).build();

List<FlownodeInstance> flownodes = client.searchFlownodeInstances(flownodeQuery);
        
//get a flownode instance by its key
FlownodeInstance flownodes = client.getFlownodeInstance(flownodes.get(0).getKey());

//search variables based on filters
VariableFilter variableFilter = new VariableFilter.Builder().processInstanceKey(4L).build();
 SearchQuery varQuery = new SearchQuery.Builder().withFilter(variableFilter).withSize(5).withSort(new Sort("name", SortOrder.ASC)).build();

List<Variable> variables = client.searchVariables(varQuery);
        
//get a variable by its key
Variable var = client.getVariable(variables.get(0).getKey());
            
//search incidents based on filters
IncidentFilter incidentFilter = new IncidentFilter.Builder().creationTime(new DateFilter(new Date(), DateFilterRange.YEAR)).build();
SearchQuery incidentQuery = new SearchQuery.Builder().withFilter(incidentFilter).withSize(20).withSort(new Sort("state", SortOrder.ASC)).build();
List<Incident> incidents = client.searchIncidents(incidentQuery);
        
//get a incident by its key
Incident incident = client.getIncident(incidents.get(0).getKey());

```
# Authentication
You can use the ***SimpleAuthentication*** to connect to a local Camunda Operate if your setup is "simple": ***without identity and keycloak***.

To connect to the **SaaS** Operate, you need to use the **SaasAuthentication** rather than the SimpleAuthentication. The SaaSAuthentication requires the ClientId and SecretId

```
SaasAuthentication sa = new SaasAuthentication("2~nB1MwkUU45FuXXX", "aBRKtreXQF3uD2MYYY");
CamundaOperateClient client = new CamundaOperateClient.Builder().authentication(sa)
    .operateUrl("https://bru-2.operate.camunda.io/757dbc30-5127-4bed-XXXX-XXXXXXXXXXXX").build();
```

You can also specify the OAuth-URL and audience, for example if you connect to a Camunda TEST system:

```
SaasAuthentication sa = new SaasAuthentication("https://login.cloud.camunda.io/oauth/token", "operate.camunda.io", 2~nB1MwkUU45FuXXX", "aBRKtreXQF3uD2MYYY");
```

To connect to the **Local** Operate with **Identity & Keycloak**, you need to use the **SelfManagedAuthentication**. The SelfManagedAuthentication requires the clientId and clientSecret. You can also change the Keycloak realm and the keycloakUrl depending on your installation.

```java
SelfManagedAuthentication sma = new SelfManagedAuthentication().clientId("java").clientSecret("foTPogjlI0hidwbDZcYFWzmU8FOQwLx0").baseUrl("http://localhost:18080").keycloakRealm("camunda-platform");
CamundaOperateClient client = new CamundaOperateClient.Builder().authentication(sma)
    .operateUrl("http://localhost:8081/").build();
```

# Use the Beta client
If you're using an older version of Camunda SaaS or you're having a local setup without Keycloak, you could also query the same APIs as Operate UI. In such a case, you might want to use the Beta client :

```java
SimpleAuthentication sa = new SimpleAuthentication("demo", "demo", "http://localhost:8081");
CamundaOperateClient client = new CamundaOperateClient.Builder().beta().operateUrl("http://localhost:8081").authentication(sa).build();

JsonNode json = ((CamundaOperateBetaClient) client).getFlowNodeStates(2L);
        
AuditTrail auditTrail = ((CamundaOperateBetaClient) client).getAuditTrail(2L);
```

Obviously, as soon as the exposed APIs will be sufficient, we should get rid of this Beta client.

# use it in your project
You can import it to your maven or gradle project as a dependency

```xml
<dependency>
	<groupId>io.camunda</groupId>
	<artifactId>camunda-operate-client-java</artifactId>
	<version>1.3.4</version>
</dependency>
```

# Note
A similar library is available for the Tasklist API of Camunda Platform 8 here:
[camunda-tasklist-client-java](https://github.com/camunda-community-hub/camunda-tasklist-client-java)
