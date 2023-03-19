package org.Talend.demo.tckdemo.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.talend.sdk.component.api.configuration.Option;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Filter implements Serializable {

    @Option
    private OPERATOR key;

    @Option
    private String value;

    public enum OPERATOR {
        CONTAINS, STARTS, ENDS
    }

}
