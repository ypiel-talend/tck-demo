package org.Talend.demo.tckdemo.config;

import lombok.Data;
import org.Talend.demo.tckdemo.migration.DSEMigration;
import org.talend.sdk.component.api.component.Version;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.type.DataSet;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;

import java.io.Serializable;
import java.util.List;

@Data
@Version(value = Datastore.VERSION, migrationHandler = DSEMigration.class)
@DataSet("Dataset")
@GridLayout({@GridLayout.Row({"datastore"}),
        @GridLayout.Row({"filters"})
})
@GridLayout(names = GridLayout.FormType.ADVANCED, value = {@GridLayout.Row({"datastore"}),
        @GridLayout.Row({"pairs"})})
public class Dataset implements Serializable {

    @Option
    private Datastore datastore;

    @Option
    private List<String> filters;

    @Option
    private List<KeyPair> pairs;

}
