package io.camunda.operate;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.io.entity.StringEntity;

import io.camunda.operate.auth.AuthInterface;
import io.camunda.operate.beta.CamundaOperateBetaClient;
import io.camunda.operate.dto.FlownodeInstance;
import io.camunda.operate.dto.Incident;
import io.camunda.operate.dto.ProcessDefinition;
import io.camunda.operate.dto.ProcessInstance;
import io.camunda.operate.dto.SearchResult;
import io.camunda.operate.dto.Variable;
import io.camunda.operate.exception.OperateException;
import io.camunda.operate.search.SearchQuery;
import io.camunda.operate.util.Java8Utils;
import io.camunda.operate.util.JsonUtils;
import io.camunda.operate.util.QueryValidatorUtils;
import io.camunda.zeebe.model.bpmn.Bpmn;
import io.camunda.zeebe.model.bpmn.BpmnModelInstance;

public class CamundaOperateClient {

  private AuthInterface authentication;

  private String operateUrl;

  private Header authHeader;

  private int tokenExpiration;

  public ProcessDefinition getProcessDefinition(Long key) throws OperateException {
    return get(key, ProcessDefinition.class);
  }
  
  public List<ProcessDefinition> searchProcessDefinitions(SearchQuery query) throws OperateException {
    QueryValidatorUtils.verifyQuery(query, ProcessDefinition.class);
    return search(query, ProcessDefinition.class).getItems();
  }

  public SearchResult<ProcessDefinition> searchProcessDefinitionResults(SearchQuery query) throws OperateException {
    QueryValidatorUtils.verifyQuery(query, ProcessDefinition.class);
    return search(query, ProcessDefinition.class);
  }

  public String getProcessDefinitionXml(Long key) throws OperateException {
    String url = operateUrl + CamundaOperateConstants.OBJECT_APIS.get(ProcessDefinition.class) + "/" + key + "/xml";

    HttpGet httpGet = new HttpGet(url);
    httpGet.addHeader(authHeader);

    try {
      return executeQuery(httpGet);
    } catch (OperateException e) {
      throw new OperateException("Error get Process Definition XML for " + key, e);
    }
  }

  public BpmnModelInstance getProcessDefinitionModel(Long key) throws OperateException {
    String url = operateUrl + CamundaOperateConstants.OBJECT_APIS.get(ProcessDefinition.class) + "/" + key + "/xml";

    HttpGet httpGet = new HttpGet(url);
    httpGet.addHeader(authHeader);

    try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
      try (CloseableHttpResponse response = execute(httpClient, httpGet)) {
        InputStream processInputStream = new ByteArrayInputStream(
            Java8Utils.readAllBytes(response.getEntity().getContent()));
        return Bpmn.readModelFromStream(processInputStream);
      }
    } catch (IOException e) {
      throw new OperateException(e);
    }
  }
  public CloseableHttpResponse execute(CloseableHttpClient httpClient, ClassicHttpRequest request) throws IOException, OperateException {
	return execute(httpClient, request, 0);
  }
  private CloseableHttpResponse execute(CloseableHttpClient httpClient, ClassicHttpRequest request, int count) throws IOException, OperateException {
    CloseableHttpResponse response = httpClient.execute(request);
    if (response.getCode()==401 && count<=2) {
    	authentication.authenticate(this);
        request.setHeader(authHeader);
    	return execute(httpClient, request, ++count);
    }
    if (response.getCode()>399) {
    	throw createDetailedException(response, request);
    }
    return response;
  }
  
  private OperateException createDetailedException(CloseableHttpResponse response, ClassicHttpRequest request) throws IOException {
	  return new OperateException(request.getPath()+" : "+response.getCode()+" "+response.getReasonPhrase());
  }

  public ProcessInstance getProcessInstance(Long key) throws OperateException {
    return get(key, ProcessInstance.class);
  }

  public List<ProcessInstance> searchProcessInstances(SearchQuery query) throws OperateException {
    QueryValidatorUtils.verifyQuery(query, ProcessInstance.class);
    return search(query, ProcessInstance.class).getItems();
  }

  public SearchResult<ProcessInstance> searchProcessInstanceResults(SearchQuery query) throws OperateException {
    QueryValidatorUtils.verifyQuery(query, ProcessInstance.class);
    return search(query, ProcessInstance.class);
  }

  public FlownodeInstance getFlownodeInstance(Long key) throws OperateException {
    return get(key, FlownodeInstance.class);
  }

  public List<FlownodeInstance> searchFlownodeInstances(SearchQuery query) throws OperateException {
    QueryValidatorUtils.verifyQuery(query, FlownodeInstance.class);
    return search(query, FlownodeInstance.class).getItems();
  }

  public SearchResult<FlownodeInstance> searchFlownodeInstanceResults(SearchQuery query) throws OperateException {
    QueryValidatorUtils.verifyQuery(query, FlownodeInstance.class);
    return search(query, FlownodeInstance.class);
  }

  public Incident getIncident(Long key) throws OperateException {
    return get(key, Incident.class);
  }

  public List<Incident> searchIncidents(SearchQuery query) throws OperateException {
    QueryValidatorUtils.verifyQuery(query, Incident.class);
    return search(query, Incident.class).getItems();
  }

  public SearchResult<Incident> searchIncidentResults(SearchQuery query) throws OperateException {
    QueryValidatorUtils.verifyQuery(query, Incident.class);
    return search(query, Incident.class);
  }

  public Variable getVariable(Long key) throws OperateException {
    return get(key, Variable.class);
  }

  public List<Variable> searchVariables(SearchQuery query) throws OperateException {
    QueryValidatorUtils.verifyQuery(query, Variable.class);
    return search(query, Variable.class).getItems();
  }

  public SearchResult<Variable> searchVariableResults(SearchQuery query) throws OperateException {
    QueryValidatorUtils.verifyQuery(query, Variable.class);
    return search(query, Variable.class);
  }
  
  public <T> SearchResult<T> search(SearchQuery query, Class<T> resultType) throws OperateException {
    if (query==null || resultType==null) {
      throw new OperateException("Query and resultType shouldn't be null");
    }
    HttpPost httpPost = new HttpPost(operateUrl + CamundaOperateConstants.OBJECT_APIS.get(resultType) + "/search");
    httpPost.addHeader("Content-Type", "application/json");
    httpPost.addHeader(authHeader);

    try {
      String data = JsonUtils.toJson(query);
      httpPost.setEntity(new StringEntity(data));
      return JsonUtils.toSearchResult(executeQuery(httpPost), resultType);
    } catch (IOException e) {
      throw new OperateException("Error executing the SearchQuery", e);
    }
  }

  private <T> T get(Long key, Class<T> resultType) throws OperateException {
    if (key==null || resultType==null) {
      throw new OperateException("Key and resultType shouldn't be null");
    }
    String url = operateUrl + CamundaOperateConstants.OBJECT_APIS.get(resultType) + "/" + key;
    HttpGet httpGet = new HttpGet(url);
    httpGet.addHeader("Content-Type", "application/json");
    httpGet.addHeader(authHeader);

    try {
      return JsonUtils.toResult(executeQuery(httpGet), resultType);
    } catch (IOException e) {
      throw new OperateException("Error executing get for (key : " + key + ")" + resultType.getName() + " on "+url,
          e);
    }
  }

  protected String executeQuery(ClassicHttpRequest httpRequest) throws OperateException {
    reconnectEventually();
    try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
      try (CloseableHttpResponse response = execute(httpClient, httpRequest)) {
        return new String(Java8Utils.readAllBytes(response.getEntity().getContent()), StandardCharsets.UTF_8);
      }
    } catch (IOException e) {
      throw new OperateException(e);
    }
  }

  public String getOperateUrl() {
    return operateUrl;
  }

  public void setOperateUrl(String operateUrl) {
    this.operateUrl = operateUrl;
  }

  public Header getAuthHeader() {
    return authHeader;
  }

  public void setAuthHeader(Header authHeader) {
    this.authHeader = authHeader;
  }

  public void setTokenExpiration(int tokenExpiration) {
    this.tokenExpiration = tokenExpiration;
  }

  private void reconnectEventually() throws OperateException {
    if (this.tokenExpiration>0 && this.tokenExpiration<(System.currentTimeMillis()/1000-3)) {
      authentication.authenticate(this);
    }
  }

  public static class Builder {

    private AuthInterface authentication;

    private String operateUrl;

    private boolean beta;

    public Builder() {

    }

    public Builder beta() {
      beta = true;
      return this;
    }

    public Builder authentication(AuthInterface authentication) {
      this.authentication = authentication;
      return this;
    }

    public Builder operateUrl(String operateUrl) {
      this.operateUrl = formatUrl(operateUrl);
      return this;
    }

    public CamundaOperateClient build() throws OperateException {
      CamundaOperateClient client;
      if (beta) {
        client = new CamundaOperateBetaClient();
      } else {
        client = new CamundaOperateClient();
      }
      client.authentication = authentication;
      client.operateUrl = operateUrl;
      authentication.authenticate(client);
      return client;
    }

    private String formatUrl(String url) {
      if (url.endsWith("/")) {
        return url.substring(0, url.length()-1);
      }
      return url;
    }
  }


}
