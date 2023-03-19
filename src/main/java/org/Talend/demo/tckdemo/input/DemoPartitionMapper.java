package org.Talend.demo.tckdemo.input;

import lombok.Data;
import org.Talend.demo.tckdemo.config.Datastore;
import org.Talend.demo.tckdemo.config.InputConfig;
import org.Talend.demo.tckdemo.migration.InputMigration;
import org.Talend.demo.tckdemo.service.I18n;
import org.Talend.demo.tckdemo.service.RuntimeService;
import org.Talend.demo.tckdemo.service.UIService;
import org.talend.sdk.component.api.component.Icon;
import org.talend.sdk.component.api.component.Version;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.input.Assessor;
import org.talend.sdk.component.api.input.Emitter;
import org.talend.sdk.component.api.input.PartitionMapper;
import org.talend.sdk.component.api.input.PartitionSize;
import org.talend.sdk.component.api.input.Split;
import org.talend.sdk.component.api.service.completion.SuggestionValues;
import org.talend.sdk.component.api.service.record.RecordBuilderFactory;

import java.io.IOException;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Version(value = Datastore.VERSION, migrationHandler = InputMigration.class)
@Icon(value = Icon.IconType.CUSTOM, custom = "demo")
@PartitionMapper(name = "Mapper")
@Data
public class DemoPartitionMapper implements Serializable {


    private InputConfig config;
    private RuntimeService service;

    private UIService uiservice;
    private I18n i18n;

    private long bundleSize;

    private int index;

    private String categ;


    public DemoPartitionMapper(@Option("configuration") final InputConfig config,
                               final RuntimeService service,
                               final UIService uiService,
                               final I18n i18n) {
        this.config = config;
        this.service = service;
        this.uiservice = uiService;
        this.i18n = i18n;
    }

    @Assessor
    public long estimateSize() {
        return 0l;
    }

    @Split
    public List<DemoPartitionMapper> split(@PartitionSize final long bundleSize) throws IOException, URISyntaxException, InterruptedException {
        SuggestionValues suggestionValuesCategs = uiservice.listCategs(this.config.getDataset().getDatastore());

        List<DemoPartitionMapper> mappers = new ArrayList<>();

        String selectCateg = this.config.getDataset().getCateg();
        if(selectCateg == null || selectCateg.trim().isEmpty()) {
            Collection<SuggestionValues.Item> items = suggestionValuesCategs.getItems();
            int index = 0;
            for (SuggestionValues.Item itCateg : items) {
                index++;
                DemoPartitionMapper mapper = new DemoPartitionMapper(config, service, uiservice, i18n);
                mapper.setIndex(index);
                mapper.setBundleSize(bundleSize);
                mapper.setCateg(itCateg.getId());
                mappers.add(mapper);
            }
        }
        else{
            this.setIndex(1);
            this.setBundleSize(bundleSize);
            this.setCateg(selectCateg);
            mappers.add(this);
        }

        return mappers;
    }

    @Emitter
    public DemoInput createSource() {
        return new DemoInput(config, service, i18n, getBundleSize(), getIndex(), getCateg());
    }

}
