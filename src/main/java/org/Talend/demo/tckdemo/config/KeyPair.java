package org.Talend.demo.tckdemo.config;

import lombok.Data;
import org.talend.sdk.component.api.configuration.Option;

import java.io.Serializable;

@Data
public class KeyPair implements Serializable {

    @Option
    private String key;

    @Option
    private String value;

}
