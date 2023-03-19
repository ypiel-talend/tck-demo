package org.Talend.demo.tckdemo.output;

import org.Talend.demo.tckdemo.config.Datastore;
import org.Talend.demo.tckdemo.config.OutputConfig;
import org.Talend.demo.tckdemo.service.RuntimeService;
import org.talend.sdk.component.api.component.Icon;
import org.talend.sdk.component.api.component.Version;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.meta.Documentation;
import org.talend.sdk.component.api.processor.AfterGroup;
import org.talend.sdk.component.api.processor.BeforeGroup;
import org.talend.sdk.component.api.processor.ElementListener;
import org.talend.sdk.component.api.processor.Input;
import org.talend.sdk.component.api.processor.Processor;
import org.talend.sdk.component.api.record.Record;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Version(Datastore.VERSION)
@Icon(value = Icon.IconType.CUSTOM, custom = "demo")
@Processor(name = "Output")
@Documentation("Send the record as json to an HTTP endpoint.")
public class DemoOutput implements Serializable {

    private HttpClient httpClient;

    private final OutputConfig config;
    private final RuntimeService service;
    private List<Record> buffer;

    public DemoOutput(@Option("configuration") OutputConfig config,
                      RuntimeService service) {
        this.config = config;
        this.service = service;
    }

    @PostConstruct
    public void init() {
        httpClient = HttpClient.newHttpClient();
    }

    @BeforeGroup
    public void beforeGroup() {
        buffer = new ArrayList<>();
    }

    @ElementListener
    public void onRecord(@Input final Record record) {
        buffer.add(record);

    }

    @AfterGroup
    public void afterGroup() throws InterruptedException, URISyntaxException, IOException {
        service.storeTodo(httpClient, this.buffer, this.config);
        this.buffer.clear();
    }

    @PreDestroy
    public void release() throws IOException, InterruptedException {
        httpClient = null;
    }


}
