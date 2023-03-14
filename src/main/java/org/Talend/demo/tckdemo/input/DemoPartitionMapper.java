package org.Talend.demo.tckdemo.input;

import lombok.Data;
import org.Talend.demo.tckdemo.config.Datastore;
import org.Talend.demo.tckdemo.config.InputConfig;
import org.Talend.demo.tckdemo.migration.InputMigration;
import org.Talend.demo.tckdemo.service.I18n;
import org.Talend.demo.tckdemo.service.RuntimeService;
import org.talend.sdk.component.api.component.Icon;
import org.talend.sdk.component.api.component.Version;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.input.Assessor;
import org.talend.sdk.component.api.input.Emitter;
import org.talend.sdk.component.api.input.PartitionMapper;
import org.talend.sdk.component.api.input.PartitionSize;
import org.talend.sdk.component.api.input.Split;
import org.talend.sdk.component.api.service.record.RecordBuilderFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Version(value = Datastore.VERSION, migrationHandler = InputMigration.class)
@Icon(value = Icon.IconType.CUSTOM, custom = "demo")
@PartitionMapper(name = "DemoMapper")
@Data
public class DemoPartitionMapper implements Serializable {


    private InputConfig config;
    private RuntimeService service;
    private I18n i18n;

    private long bundleSize;

    private int index;


    public DemoPartitionMapper(@Option("configuration") final InputConfig config,
                               final RuntimeService service,
                               final I18n i18n) {
        this.config = config;
        this.service = service;
        this.i18n = i18n;
    }

    @Assessor
    public long estimateSize() {
        return this.config.getSize();
    }

    @Split
    public List<DemoPartitionMapper> split(@PartitionSize final long bundleSize) {
        List<DemoPartitionMapper> mappers = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            DemoPartitionMapper mapper = new DemoPartitionMapper(config, service, i18n);
            mapper.setIndex(i);
            mapper.setBundleSize(bundleSize);
            mappers.add(mapper);
        }

        return mappers;
    }

    @Emitter
    public DemoInput createSource() {
        return new DemoInput(config, service, i18n, this.bundleSize, getIndex());
    }

}
