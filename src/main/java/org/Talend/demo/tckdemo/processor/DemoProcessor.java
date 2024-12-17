package org.Talend.demo.tckdemo.processor;

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
import org.talend.sdk.component.api.processor.Output;
import org.talend.sdk.component.api.processor.OutputEmitter;
import org.talend.sdk.component.api.processor.Processor;
import org.talend.sdk.component.api.record.Record;
import org.talend.sdk.component.api.service.record.RecordBuilderFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Version(Datastore.VERSION)
@Icon(value = Icon.IconType.CUSTOM, custom = "demo")
@Processor(name = "Processor")
@Documentation("Send the record as json to an HTTP endpoint and return inserted todos.")
public class DemoProcessor implements Serializable {

    private HttpClient httpClient;

    private final OutputConfig config;
    private final RuntimeService service;
    private final RecordBuilderFactory recordBuilderFactory;
    private List<Record> buffer;

    public DemoProcessor(@Option("configuration") OutputConfig config,
                         RuntimeService service,
                         RecordBuilderFactory recordBuilderFactory) {
        this.config = config;
        this.service = service;
        this.recordBuilderFactory = recordBuilderFactory;
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
    public void afterGroup(@Output final OutputEmitter<Record> inserted,
                           @Output("REJECT") final OutputEmitter<Record> rejected) throws InterruptedException, URISyntaxException, IOException {
        Map<String, List<String>> result = service.storeTodo(httpClient, this.buffer, this.config);
        for (String todo : result.get("inserted")) {
            inserted.emit(recordBuilderFactory.newRecordBuilder().withString("todo", todo).build());
        }
        for (String todo : result.get("rejected")) {
            rejected.emit(recordBuilderFactory.newRecordBuilder().withString("todo", todo).build());
        }
        this.buffer.clear();
    }

    @PreDestroy
    public void release() throws IOException, InterruptedException {
        httpClient = null;
    }

}
