package org.Talend.demo.tckdemo.migration;

import org.talend.sdk.component.api.component.MigrationHandler;

import java.util.HashMap;
import java.util.Map;

public class DSOMigration implements MigrationHandler {
    public Map<String, String> migrate(int incomingVersion, Map<String, String> incomingData) {
        Map<String, String> duplicate = new HashMap<String, String>(incomingData);

        duplicate.putIfAbsent("configuration.dataset.datastore.port", "3310");

        return duplicate;
    }
}
