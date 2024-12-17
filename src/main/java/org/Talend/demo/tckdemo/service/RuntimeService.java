package org.Talend.demo.tckdemo.service;

import org.Talend.demo.tckdemo.config.Datastore;
import org.Talend.demo.tckdemo.config.Filter;
import org.Talend.demo.tckdemo.config.InputConfig;
import org.Talend.demo.tckdemo.config.OutputConfig;
import org.talend.sdk.component.api.exception.ComponentException;
import org.talend.sdk.component.api.record.Record;
import org.talend.sdk.component.api.service.Service;
import org.talend.sdk.component.api.service.record.RecordBuilderFactory;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class RuntimeService implements Serializable {

    @Service
    private RecordBuilderFactory recordBuilderFactory;

    public static URI getURI(Datastore datastore, String path) throws URISyntaxException {
        return new URI("http", null, datastore.getIp(), datastore.getPort(), path, null, null);
    }

    public Map<String, List<String>> storeTodo(HttpClient httpClient, List<Record> buffer, OutputConfig config) throws InterruptedException, URISyntaxException, IOException {
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        for (Record r : buffer) {
            JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
            objectBuilder.add("categ", r.getString("categ"))
                    .add("todo", r.getString("todo"));
            arrayBuilder.add(objectBuilder.build());
        }

        JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
        objectBuilder.add("todos", arrayBuilder.build());
        objectBuilder.add("createCategory", config.isCreateCateg());

        URI uri = getURI(config.getDataset().getDatastore(), "/todo/bulkAdd");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + config.getDataset().getDatastore().getBearer().getToken())
                .header("Categ", config.getDataset().getCateg())
                .POST(HttpRequest.BodyPublishers.ofString(objectBuilder.build().toString()))
                .build();

        HttpResponse<String> httpResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        JsonReader reader = Json.createReader(new StringReader(httpResponse.body()));
        JsonObject payload = reader.readObject();

        Map<String, List<String>> result = new HashMap<>();
        result.put("inserted", new ArrayList<>());
        result.put("rejected", new ArrayList<>());

        payload.getJsonArray("added").forEach(v -> result.get("inserted").add(v.toString()));
        payload.getJsonArray("rejected").forEach(v -> result.get("rejected").add(v.toString()));

        if (httpResponse.statusCode() != 200) {
            throw new ComponentException("Store todo has failed, status:" + httpResponse.statusCode());
        }

        TimeUnit.MILLISECONDS.sleep(config.getThrottling());

        return result;
    }

    public void reset(Datastore dso) throws URISyntaxException, IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = getURI(dso, "/reset");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();

        client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public List<Record> getTodo(InputConfig config, String categ, int mapperIndex) throws URISyntaxException, IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        if (categ == null) {
            categ = "";
        }

        URI uri = getURI(config.getDataset().getDatastore(), "/todo/listOneCateg");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Categ", categ)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        List<Record> todos = new ArrayList<>();

        JsonReader reader = Json.createReader(new StringReader(response.body()));
        JsonObject jsonCateg = reader.readObject();
        if (!jsonCateg.containsKey(categ)) {
            return todos;
        }

        JsonArray array = jsonCateg.getJsonArray(categ);
        for (int i = 0; i < array.size(); i++) {
            JsonObject jsonObject = array.get(i).asJsonObject();
            Record.Builder builder = recordBuilderFactory.newRecordBuilder().withString("categ", categ)
                    .withString("todo", jsonObject.getString("todo"))
                    .withBoolean("done", jsonObject.getBoolean("done"));

            if (config.isAddMapperIndex()) {
                builder.withInt("mapperIndex", mapperIndex);
            }

            Record record = builder.build();
            if (filter(record, config.getDataset().getFilters())) {
                todos.add(record);
            }
        }

        return todos;
    }

    private boolean filter(Record record, List<Filter> filters) {
        boolean ok = false;
        if (filters == null || filters.size() <= 0) {
            return true;
        }
        for (Filter kp : filters) {
            String todo = record.getString("todo");
            switch (kp.getKey()) {
                case STARTS:
                    ok = todo.startsWith(kp.getValue());
                    break;
                case ENDS:
                    ok = todo.endsWith(kp.getValue());
                    break;
                case CONTAINS:
                    ok = todo.contains(kp.getValue());
                    break;
            }
            if (ok) {
                return true;
            }
        }
        return false;
    }
}
