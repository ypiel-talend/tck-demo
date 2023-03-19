package org.Talend.demo.tckdemo.config;

import lombok.Data;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.ui.DefaultValue;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;

import java.io.Serializable;

@Data
@GridLayout({@GridLayout.Row({"dataset"}),
        @GridLayout.Row({"addMapperIndex"})
})
@GridLayout(names = GridLayout.FormType.ADVANCED, value = {@GridLayout.Row({"dataset"}),
        @GridLayout.Row({"logLevel"})})
public class InputConfig implements Serializable {

    @Option
    private Dataset dataset;

    @Option
    private boolean addMapperIndex;

    @Option
    @DefaultValue("NONE")
    private LogLevel logLevel;

    public enum LogLevel {
        DEBUG, INFO, WARN, ERROR, NONE;
    }

}
