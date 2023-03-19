package org.Talend.demo.tckdemo.output;

import org.Talend.demo.tckdemo.config.Dataset;
import org.Talend.demo.tckdemo.config.Datastore;
import org.Talend.demo.tckdemo.config.InputConfig;
import org.Talend.demo.tckdemo.config.auth.Bearer;
import org.Talend.demo.tckdemo.input.DemoInputTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.talend.sdk.component.api.record.Record;
import org.talend.sdk.component.api.service.Service;
import org.talend.sdk.component.api.service.record.RecordBuilderFactory;
import org.talend.sdk.component.junit.BaseComponentsHandler;
import org.talend.sdk.component.junit5.Injected;
import org.talend.sdk.component.junit5.WithComponents;
import org.talend.sdk.component.runtime.manager.chain.Job;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.talend.sdk.component.junit.SimpleFactory.configurationByExample;

@WithComponents(value = "org.Talend.demo.tckdemo")
public class DemoOutputTest {
    @Injected
    private BaseComponentsHandler handler;

    @Service
    private RecordBuilderFactory recordBuilderFactory;

    private InputConfig config;

    @BeforeEach
    void buildConfig() {
        handler.injectServices(this);

        config = new InputConfig();
        Dataset dse = new Dataset();
        Datastore dso = new Datastore();
        dse.setDatastore(dso);
        config.setDataset(dse);

        dso.setUseAuthent(true);
        dso.setAuthentType(Datastore.AuthentTypes.BEARER);
        Bearer b = new Bearer();
        b.setToken("Bearer 1234567");
        dso.setBearer(b);

        dso.setIp("127.0.0.1");
        dso.setPort(DemoInputTest.PORT);
    }

    @Test
    public void testBulkStore() {
        config.getDataset().setCateg("test");
        List<Record> data = new ArrayList<>();

        for (int i = 1; i <= 5; i++) {
            Record todo = recordBuilderFactory.newRecordBuilder()
                    .withString("todo", "New todo #" + i)
                    .build();
            data.add(todo);
        }

        this.handler.setInputData(data);

        final String configStr = configurationByExample().forInstance(config).configured().toQueryString();
        Job
                .components() //
                .component("emitter", "test://emitter") //
                .component("out", "Demo://Output?" + configStr) //
                .connections() //
                .from("emitter") //
                .to("out") //
                .build() //
                .run();

    }
}