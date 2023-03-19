package org.Talend.demo.tckdemo.config;

import lombok.Data;
import org.Talend.demo.tckdemo.config.auth.Basic;
import org.Talend.demo.tckdemo.config.auth.Bearer;
import org.Talend.demo.tckdemo.config.auth.PAT;
import org.Talend.demo.tckdemo.migration.DSOMigration;
import org.talend.sdk.component.api.component.Version;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.condition.ActiveIf;
import org.talend.sdk.component.api.configuration.condition.ActiveIfs;
import org.talend.sdk.component.api.configuration.constraint.Pattern;
import org.talend.sdk.component.api.configuration.constraint.Required;
import org.talend.sdk.component.api.configuration.type.DataStore;
import org.talend.sdk.component.api.configuration.ui.DefaultValue;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;

import java.io.Serializable;

@Data
@DataStore("Datastore")
@Version(value = Datastore.VERSION, migrationHandler = DSOMigration.class)
@GridLayout({@GridLayout.Row({"ip", "port"}),
        @GridLayout.Row({"useAuthent"}),
        @GridLayout.Row({"authentType"}),
        @GridLayout.Row({"basic"}),
        @GridLayout.Row({"bearer"}),
        @GridLayout.Row({"pat"})
})
@GridLayout(names = GridLayout.FormType.ADVANCED, value = {@GridLayout.Row({"secure"})})
public class Datastore implements Serializable {

    public final static int VERSION = 1;

    @Option
    @Required
    @Pattern("^[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}$")
    private String ip;

    @Option
    private Integer port;

    @Option
    @DefaultValue("false")
    private boolean secure;

    @Option
    @DefaultValue("false")
    private boolean useAuthent;

    @Option
    @DefaultValue("Basic")
    @ActiveIf(target = "useAuthent", value = "true")
    private AuthentTypes authentType;

    @Option
    @ActiveIfs({
            @ActiveIf(target = "useAuthent", value = "true"),
            @ActiveIf(target = "authentType", value = "Basic")
    })
    private Basic basic;

    @Option
    @ActiveIfs({
            @ActiveIf(target = "useAuthent", value = "true"),
            @ActiveIf(target = "authentType", value = "PAT")
    })
    private PAT pat;

    @Option
    @ActiveIfs({
            @ActiveIf(target = "useAuthent", value = "true"),
            @ActiveIf(target = "authentType", value = "BEARER")
    })
    private Bearer bearer;

    public enum AuthentTypes {
        Basic, PAT, BEARER;
    }

}
