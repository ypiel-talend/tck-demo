package org.Talend.demo.tckdemo.service;

import org.Talend.demo.tckdemo.config.Datastore;
import org.Talend.demo.tckdemo.config.InputConfig;
import org.talend.sdk.component.api.record.Record;
import org.talend.sdk.component.api.service.Service;
import org.talend.sdk.component.api.service.record.RecordBuilderFactory;

import java.io.Serializable;

@Service
public class RuntimeService implements Serializable {

    @Service
    private RecordBuilderFactory recordBuilderFactory;

    public Record buildARecord(int recordIndex, int mapperIndex, long bundleSize, InputConfig config) {
        Record.Builder recordBuilder = recordBuilderFactory.newRecordBuilder();

        if (config.getDataset().getDatastore().isUseAuthent() &&
                config.getDataset().getDatastore().getAuthentType() == Datastore.AuthentTypes.Basic) {
            recordBuilder.withString("login", config.getDataset().getDatastore().getBasic().getLogin());
            recordBuilder.withString("login", config.getDataset().getDatastore().getBasic().getPassword());
        } else if (config.getDataset().getDatastore().isUseAuthent() &&
                config.getDataset().getDatastore().getAuthentType() == Datastore.AuthentTypes.PAT) {
            recordBuilder.withString("PAT", config.getDataset().getDatastore().getPat().getPat());
        }

        recordBuilder.withLong("bundleSize", bundleSize)
                .withInt("mapperIndex", mapperIndex)
                .withInt("recordIndex", recordIndex);

        return recordBuilder.build();
    }

}
