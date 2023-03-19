package org.Talend.demo.tckdemo.service;

import org.Talend.demo.tckdemo.config.Datastore;
import org.talend.sdk.component.api.service.Service;
import org.talend.sdk.component.api.service.completion.SuggestionValues;
import org.talend.sdk.component.api.service.completion.Suggestions;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
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

@Service
public class UIService implements Serializable {

    public final static String LIST_TODO_CATEG = "LIST_TODO_CATEG";

    @Suggestions(LIST_TODO_CATEG)
    public SuggestionValues listCategs(Datastore datastore) throws IOException, InterruptedException, URISyntaxException {
        HttpClient client = HttpClient.newHttpClient();

        URI uri = new URI("http", null, datastore.getIp(), datastore.getPort(), "/categ/list", null, null);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        List<SuggestionValues.Item> items = new ArrayList<>();

        JsonReader reader = Json.createReader(new StringReader(response.body()));
        JsonArray array = reader.readArray();
        for (int i = 0; i < array.size(); i++) {
            JsonObject jsonObject = array.get(i).asJsonObject();
            String name = jsonObject.getString("name");
            items.add(new SuggestionValues.Item(name, name));
        }

        return new SuggestionValues(false, items);
    }


}
