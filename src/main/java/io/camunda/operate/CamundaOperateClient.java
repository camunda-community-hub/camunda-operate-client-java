package io.camunda.operate;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.io.entity.StringEntity;

import io.camunda.operate.auth.AuthInterface;
import io.camunda.operate.dto.FlownodeInstance;
import io.camunda.operate.dto.Incident;
import io.camunda.operate.dto.ProcessDefinition;
import io.camunda.operate.dto.ProcessInstance;
import io.camunda.operate.dto.SearchResult;
import io.camunda.operate.dto.Variable;
import io.camunda.operate.exception.OperateException;
import io.camunda.operate.search.FlownodeInstanceFilter;
import io.camunda.operate.search.IncidentFilter;
import io.camunda.operate.search.ProcessDefinitionFilter;
import io.camunda.operate.search.ProcessInstanceFilter;
import io.camunda.operate.search.SearchQuery;
import io.camunda.operate.search.VariableFilter;
import io.camunda.operate.util.JsonUtils;
import io.camunda.zeebe.model.bpmn.Bpmn;
import io.camunda.zeebe.model.bpmn.BpmnModelInstance;

public class CamundaOperateClient {

    private String operateUrl;

    private Header authHeader;

    public ProcessDefinition getProcessDefinition(Long key) throws OperateException {
        return get(key, ProcessDefinition.class);
    }

    public List<ProcessDefinition> searchProcessDefinitions(SearchQuery query) throws OperateException {
        if (query.getFilter() != null && !(query.getFilter() instanceof ProcessDefinitionFilter)) {
            throw new OperateException(
                    "You should rely on ProcessDefinitionFilter for searching on processDefinitions");
        }

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
            try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                InputStream processInputStream = new ByteArrayInputStream(
                        response.getEntity().getContent().readAllBytes());
                return Bpmn.readModelFromStream(processInputStream);
            }
        } catch (IOException e) {
            throw new OperateException(e);
        }
    }

    public ProcessInstance getProcessInstance(Long key) throws OperateException {
        return get(key, ProcessInstance.class);
    }

    public List<ProcessInstance> searchProcessInstances(SearchQuery query) throws OperateException {
        if (query.getFilter() != null && !(query.getFilter() instanceof ProcessInstanceFilter)) {
            throw new OperateException("You should rely on ProcessInstanceFilter for searching on processInstances");
        }

        return search(query, ProcessInstance.class);
    }

    public FlownodeInstance getFlownodeInstance(Long key) throws OperateException {
        return get(key, FlownodeInstance.class);
    }

    public List<FlownodeInstance> searchFlownodeInstances(SearchQuery query) throws OperateException {
        if (query.getFilter() != null && !(query.getFilter() instanceof FlownodeInstanceFilter)) {
            throw new OperateException("You should rely on FlownodeInstanceFilter for searching on flownodeInstances");
        }

        return search(query, FlownodeInstance.class);
    }

    public Incident getIncident(Long key) throws OperateException {
        return get(key, Incident.class);
    }

    public List<Incident> searchIncidents(SearchQuery query) throws OperateException {
        if (query.getFilter() != null && !(query.getFilter() instanceof IncidentFilter)) {
            throw new OperateException("You should rely on IncidentFilter for searching on incidents");
        }

        return search(query, Incident.class);
    }

    public Variable getVariable(Long key) throws OperateException {
        return get(key, Variable.class);
    }

    public List<Variable> searchVariables(SearchQuery query) throws OperateException {
        if (query.getFilter() != null && !(query.getFilter() instanceof VariableFilter)) {
            throw new OperateException("You should rely on VariableFilter for searching on variables");
        }

        return search(query, Variable.class);
    }

    private <T> List<T> search(SearchQuery query, Class<T> resultType) throws OperateException {
        HttpPost httpPost = new HttpPost(operateUrl + CamundaOperateConstants.OBJECT_APIS.get(resultType) + "/search");
        httpPost.addHeader("Content-Type", "application/json");
        httpPost.addHeader(authHeader);

        try {
            String data = JsonUtils.toJson(query);
            httpPost.setEntity(new StringEntity(data));
            SearchResult<T> result = JsonUtils.toSearchResult(executeQuery(httpPost), resultType);
            return result.getItems();
        } catch (IOException e) {
            throw new OperateException("Error executing the SearchQuery", e);
        }
    }

    private <T> T get(Long key, Class<T> resultType) throws OperateException {
        HttpGet httpGet = new HttpGet(operateUrl + CamundaOperateConstants.OBJECT_APIS.get(resultType) + "/" + key);
        httpGet.addHeader("Content-Type", "application/json");
        httpGet.addHeader(authHeader);

        try {
            return JsonUtils.toResult(executeQuery(httpGet), resultType);
        } catch (IOException e) {
            throw new OperateException("Error executing get for (key : " + key + ")" + resultType.descriptorString(),
                    e);
        }
    }

    private String executeQuery(ClassicHttpRequest httpRequest) throws OperateException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            try (CloseableHttpResponse response = httpClient.execute(httpRequest)) {
                return new String(response.getEntity().getContent().readAllBytes(), StandardCharsets.UTF_8);
            }
        } catch (IOException e) {
            throw new OperateException(e);
        }
    }

    public void setAuthHeader(Header authHeader) {
        this.authHeader = authHeader;
    }

    public static class Builder {

        private AuthInterface authentication;

        private String operateUrl;

        public Builder() {

        }

        public Builder authentication(AuthInterface authentication) {
            this.authentication = authentication;
            return this;
        }

        public Builder operateUrl(String operateUrl) {
            this.operateUrl = operateUrl;
            return this;
        }

        public CamundaOperateClient build() throws OperateException {
            CamundaOperateClient client = new CamundaOperateClient();
            client.operateUrl = operateUrl;
            authentication.authenticate(client);
            return client;
        }
    }
}
