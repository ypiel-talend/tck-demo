package org.Talend.demo.tckdemo.service;

import org.Talend.demo.tckdemo.config.Datastore;
import org.Talend.demo.tckdemo.config.InputConfig;
import org.Talend.demo.tckdemo.config.Filter;
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
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class RuntimeService implements Serializable {

    @Service
    private RecordBuilderFactory recordBuilderFactory;

    private URI getURI(Datastore datastore, String path) throws URISyntaxException {
        return new URI("http", null, datastore.getIp(), datastore.getPort(), path, null, null);
    }

    public void storeTodo(HttpClient httpClient, List<Record> buffer, OutputConfig config) throws InterruptedException, URISyntaxException, IOException {
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        for (Record r : buffer) {
            JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
            objectBuilder.add("categ", config.getDataset().getCateg())
                    .add("todo", r.getString("todo"));
            arrayBuilder.add(objectBuilder.build());
        }

        URI uri = getURI(config.getDataset().getDatastore(), "/todo/bulkAdd");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Content-Type", "application/json")
                .header("Authorization", config.getDataset().getDatastore().getBearer().getToken())
                .header("Categ", config.getDataset().getCateg())
                .POST(HttpRequest.BodyPublishers.ofString(arrayBuilder.build().toString()))
                .build();

        HttpResponse<String> httpResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (httpResponse.statusCode() != 200) {
            throw new ComponentException("Store todoa has failed, status:" + httpResponse.statusCode());
        }

        TimeUnit.MILLISECONDS.sleep(config.getThrottling());
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
