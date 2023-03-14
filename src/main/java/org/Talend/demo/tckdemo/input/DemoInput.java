package org.Talend.demo.tckdemo.input;

import org.Talend.demo.tckdemo.config.InputConfig;
import org.Talend.demo.tckdemo.service.I18n;
import org.Talend.demo.tckdemo.service.RuntimeService;
import org.talend.sdk.component.api.input.Producer;
import org.talend.sdk.component.api.record.Record;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.io.Serializable;


public class DemoInput implements Serializable {

    private InputConfig config;
    private RuntimeService service;
    private I18n i18n;
    private long bundleSize;
    private int mapperIndex;

    private int recordIndex = 0;


    public DemoInput(final InputConfig config,
                     final RuntimeService service,
                     final I18n i18n,
                     final long bundleSize,
                     final int mapperIndex) {
        this.config = config;
        this.service = service;
        this.i18n = i18n;
        this.bundleSize = bundleSize;
        this.mapperIndex = mapperIndex;
    }

    @PostConstruct
    public void init() throws IOException {
        //Here we can init connections
    }

    @Producer
    public Record next() {
        if(this.recordIndex > this.mapperIndex){
            return null;
        }
        recordIndex++;

        return service.buildARecord(recordIndex, mapperIndex, bundleSize, config);
    }

    @PreDestroy
    public void release() {
        // clean and release any resources
    }

}
