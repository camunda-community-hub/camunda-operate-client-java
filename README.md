[![Community Extension](https://img.shields.io/badge/Community%20Extension-An%20open%20source%20community%20maintained%20project-FF4700)](https://github.com/camunda-community-hub/community)
![Compatible with: Camunda Platform 8](https://img.shields.io/badge/Compatible%20with-Camunda%20Platform%208-0072Ce)
[![](https://img.shields.io/badge/Lifecycle-Incubating-blue)](https://github.com/Camunda-Community-Hub/community/blob/main/extension-lifecycle.md#incubating-)

# Camunda Operate Client

This project is designed to simplify communication between a Java backend and the [Operate API of Camunda Platform 8](https://docs.camunda.io/docs/apis-clients/operate-api/).

## How to build the client

### Spring Boot

Add the dependency to your project:

```xml
<dependency>
  <groupId>io.camunda.spring</groupId>
  <artifactId>spring-boot-starter-camunda-operate</artifactId>
  <version>${version.operate-client}</version>
</dependency>
```

Configure a Camunda Operate client with simple authentication:

```yaml
operate:
  client:
    profile: simple
```

To adjust the (meaningful) default properties, you can also override them:

```yaml
operate:
  client:
    profile: simple
    enabled: true
    base-url: http://localhost:8081
    session-timeout: PT10M
    username: demo
    password: demo
```


Configure a Camunda Operate client with identity authentication:

```yaml
operate:
  client:
    profile: oidc
    client-id:
    client-secret:
    scope: # optional
```

To adjust the (meaningful) default properties, you can also override them:

```yaml
operate:
  client:
    profile: oidc
    enabled: true
    base-url: http://localhost:8081
    auth-url: http://localhost:18080/auth/realms/camunda-platform/protocol/openid-connect/token
    audience: operate-api
    client-id:
    client-secret:
    scope: # optional
```

Configure a Camunda Operate client for Saas:

```yaml
operate:
  client:
    profile: saas
    region:
    cluster-id:
    client-id:
    client-secret:
```

To adjust the (meaningful) default properties, you can also override them:

```yaml
operate:
  client:
    profile: saas
    enabled: true
    base-url: https://${operate.client.region}.operate.camunda.io/${operate.client.cluster-id}
    auth-url: https://login.cloud.camunda.io/oauth/token
    audience: operate.camunda.io
    region:
    cluster-id:
    client-id:
    client-secret:
```

### Plain Java

Add the dependency to your project:

```xml
<dependency>
  <groupId>io.camunda.spring</groupId>
  <artifactId>java-client-operate</artifactId>
  <version>${version.operate-client}</version>
</dependency>
```

Build a Camunda Operate client with simple authentication:

```java
// properties you need to provide
String username = "demo";
String password = "demo";
URL operateUrl = URI.create("http://localhost:8081").toURL();
// bootstrapping
SimpleCredential credentials =
    new SimpleCredential(username, password, operateUrl, Duration.ofMinutes(10));
SimpleAuthentication authentication = new SimpleAuthentication(credentials);
ObjectMapper objectMapper = new ObjectMapper();
CamundaOperateClientConfiguration configuration =
    new CamundaOperateClientConfiguration(
        authentication, operateUrl, objectMapper, HttpClients.createDefault());
CamundaOperateClient client = new CamundaOperateClient(configuration);
```

Build a Camunda Operate client with identity authentication:

```java
// properties you need to provide
String clientId = "";
String clientSecret = "";
String audience = "operate-api";
String scope = ""; // can be omitted if not required
URL operateUrl = URI.create("http://localhost:8081").toURL();
URL authUrl =
    URI.create(
           "http://localhost:18080/auth/realms/camunda-platform/protocol/openid-connect/token")
       .toURL();
// bootstrapping
JwtCredential credentials = new JwtCredential(clientId, clientSecret, audience, authUrl, scope);
ObjectMapper objectMapper = new ObjectMapper();
TokenResponseMapper tokenResponseMapper = new JacksonTokenResponseMapper(objectMapper);
JwtAuthentication authentication = new JwtAuthentication(credentials, tokenResponseMapper);
CamundaOperateClientConfiguration configuration =
    new CamundaOperateClientConfiguration(
        authentication, operateUrl, objectMapper, HttpClients.createDefault());
CamundaOperateClient client = new CamundaOperateClient(configuration);
```

Build a Camunda Operate client for Saas:

```java
// properties you need to provide
String region = "";
String clusterId = "";
String clientId = "";
String clientSecret = "";
// bootstrapping
URL operateUrl = URI.create("https://" + region + ".operate.camunda.io/" + clusterId).toURL();
URL authUrl = URI.create("https://login.cloud.camunda.io/oauth/token").toURL();
JwtCredential credentials =
    new JwtCredential(clientId, clientSecret, "operate.camunda.io", authUrl, null);
ObjectMapper objectMapper = new ObjectMapper();
TokenResponseMapper tokenResponseMapper = new JacksonTokenResponseMapper(objectMapper);
JwtAuthentication authentication = new JwtAuthentication(credentials, tokenResponseMapper);
CamundaOperateClientConfiguration configuration =
    new CamundaOperateClientConfiguration(
        authentication, operateUrl, objectMapper, HttpClients.createDefault());
CamundaOperateClient client = new CamundaOperateClient(configuration);
```

## Getting and Searching

When you search objects, you can get results as List or as SearchResult. The SearchResult gives you a sortValues that you can use to paginate your results :

```java
SearchQuery query = SearchQuery.builder().filter(someFilter).sort(new Sort("name", SortOrder.ASC)).size(20).searchAfter(previousResult.getSortValues()).build();
```

### Process definitions

```java
//Get a process definition by its key
ProcessDefinition def = client.getProcessDefinition(1L);

//Search process definitions
ProcessDefinitionFilter processDefinitionFilter = ProcessDefinitionFilter.builder().name("Customer Onboarding").build();
SearchQuery<ProcessDefinition> procDefQuery = SearchQuery.<ProcessDefinition>builder().filter(processDefinitionFilter).size(20).sort(new Sort("version", SortOrder.ASC)).build();

List<ProcessDefinition> list = client.searchProcessDefinitions(procDefQuery);

SearchResult<ProcessDefinition> result = client.searchProcessDefinitionResults(procDefQuery);
```

### Process Instances

```java
//search process instances based on filters
ProcessInstanceFilter instanceFilter = ProcessInstanceFilter.builder().bpmnProcessId("customer_onboarding_en").startDate(OperateDate.filter(new Date(), DateFilterRange.MONTH)).build();
SearchQuery<ProcessInstance> instanceQuery = SearchQuery.<ProcessInstance>builder().filter(instanceFilter).size(20).sort(new Sort("state", SortOrder.ASC)).build();

List<ProcessInstance> list = client.searchProcessInstances(instanceQuery);

SearchResult<ProcessInstance> result = client.searchProcessInstanceResults(instanceQuery);

//get a process instance by its key
ProcessInstance instance = client.getProcessInstance(instances.get(0).getKey());
```

## Note
A similar library is available for the Tasklist API of Camunda Platform 8 here:
[camunda-tasklist-client-java](https://github.com/camunda-community-hub/camunda-tasklist-client-java)
