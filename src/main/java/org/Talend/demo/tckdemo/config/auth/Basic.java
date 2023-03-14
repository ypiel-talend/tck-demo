package org.Talend.demo.tckdemo.config.auth;

import lombok.Data;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;
import org.talend.sdk.component.api.configuration.ui.widget.Credential;

import java.io.Serializable;

@Data
@GridLayout({@GridLayout.Row({"login"}), @GridLayout.Row({"password"})})
public class Basic implements Serializable {

    @Option
    private String login;

    @Option
    @Credential
    private String password;

}
