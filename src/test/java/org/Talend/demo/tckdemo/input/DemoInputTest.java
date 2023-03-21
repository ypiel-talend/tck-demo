package org.Talend.demo.tckdemo.input;

import org.Talend.demo.tckdemo.config.Dataset;
import org.Talend.demo.tckdemo.config.Datastore;
import org.Talend.demo.tckdemo.config.InputConfig;
import org.Talend.demo.tckdemo.config.Filter;
import org.Talend.demo.tckdemo.config.auth.Bearer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.talend.sdk.component.api.record.Record;
import org.talend.sdk.component.junit.BaseComponentsHandler;
import org.talend.sdk.component.junit5.Injected;
import org.talend.sdk.component.junit5.WithComponents;
import org.talend.sdk.component.runtime.manager.chain.Job;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.talend.sdk.component.junit.SimpleFactory.configurationByExample;

@WithComponents(value = "org.Talend.demo.tckdemo")
public class DemoInputTest {

    public final static int PORT = 8989;

    @Injected
    private BaseComponentsHandler handler;

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
        dso.setPort(PORT);
    }

    @Test
    public void testFilters() {
        this.config.getDataset().setFilters(Arrays.asList(new Filter(Filter.OPERATOR.CONTAINS, "km")));

        final String configStr = configurationByExample().forInstance(config).configured().toQueryString();

        Job
                .components() //
                .component("emitter", "Demo://Mapper?" + configStr) //
                .component("out", "test://collector") //
                .connections() //
                .from("emitter") //
                .to("out") //
                .build() //
                .run();

        final List<Record> records = handler.getCollectedData(Record.class);

        assertEquals(2, records.size());
    }

    @Test
    public void testMissingCateg() {
        this.config.getDataset().setCateg("Unknown");

        final String configStr = configurationByExample().forInstance(config).configured().toQueryString();

        Job
                .components() //
                .component("emitter", "Demo://Mapper?" + configStr) //
                .component("out", "test://collector") //
                .connections() //
                .from("emitter") //
                .to("out") //
                .build() //
                .run();

        final List<Record> records = handler.getCollectedData(Record.class);

        assertEquals(0, records.size());
    }

}