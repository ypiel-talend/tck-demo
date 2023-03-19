package org.Talend.demo.tckdemo.config;

import lombok.Data;
import org.Talend.demo.tckdemo.migration.DSEMigration;
import org.Talend.demo.tckdemo.service.UIService;
import org.talend.sdk.component.api.component.Version;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.action.Suggestable;
import org.talend.sdk.component.api.configuration.type.DataSet;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;

import java.io.Serializable;
import java.util.List;

@Data
@Version(value = Datastore.VERSION, migrationHandler = DSEMigration.class)
@DataSet("Dataset")
@GridLayout({@GridLayout.Row({"datastore"}),
        @GridLayout.Row({"categ"}),
        @GridLayout.Row({"filters"})
})
@GridLayout(names = GridLayout.FormType.ADVANCED, value = {@GridLayout.Row({"datastore"}),
        @GridLayout.Row({"someAdvanced"})})
public class Dataset implements Serializable {

    @Option
    private Datastore datastore;

    @Option
    @Suggestable(value = UIService.LIST_TODO_CATEG, parameters = "datastore")
    private String categ;

    @Option
    private List<Filter> filters;

    @Option
    private String someAdvanced;

}
