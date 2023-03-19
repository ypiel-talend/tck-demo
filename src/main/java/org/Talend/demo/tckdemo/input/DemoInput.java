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
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.util.Iterator;
import java.util.List;


public class DemoInput implements Serializable {

    private InputConfig config;
    private RuntimeService service;
    private I18n i18n;
    private long bundleSize;
    private int mapperIndex;

    private int recordIndex = 0;

    private String categ;

    private Iterator<Record> iterator = null;


    public DemoInput(final InputConfig config,
                     final RuntimeService service,
                     final I18n i18n,
                     final long bundleSize,
                     final int mapperIndex,
                     final String categ) {
        this.config = config;
        this.service = service;
        this.i18n = i18n;
        this.bundleSize = bundleSize;
        this.mapperIndex = mapperIndex;
        this.categ = categ;
    }

    @PostConstruct
    public void init() throws IOException, URISyntaxException, InterruptedException {
        List<Record> todos = service.getTodo(this.config, this.categ, this.mapperIndex);
        iterator = todos.iterator();
    }

    @Producer
    public Record next() {
       if(iterator == null || !iterator.hasNext()){
           return null;
       }

       return iterator.next();
    }

    @PreDestroy
    public void release() {
        // clean and release any resources
        iterator = null;
    }

}
