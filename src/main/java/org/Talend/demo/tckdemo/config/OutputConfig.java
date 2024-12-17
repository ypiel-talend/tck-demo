package org.Talend.demo.tckdemo.config;

import lombok.Data;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;

import java.io.Serializable;

@Data
@GridLayout({@GridLayout.Row({"dataset"}),
        @GridLayout.Row({"createCateg"}),
        @GridLayout.Row({"throttling"})})
@GridLayout(names = GridLayout.FormType.ADVANCED, value = {@GridLayout.Row({"dataset"}) })
public class OutputConfig implements Serializable {

    @Option
    private Dataset dataset;

    @Option
    private boolean createCateg;

    @Option
    private int throttling;

}
