package org.Talend.demo.tckdemo.config.auth;

import lombok.Data;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;

import java.io.Serializable;

@Data
@GridLayout({@GridLayout.Row({"token"})})
public class Bearer implements Serializable {

    @Option
    private String token;

}
