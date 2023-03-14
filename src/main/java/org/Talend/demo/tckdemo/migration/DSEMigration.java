package org.Talend.demo.tckdemo.migration;

import org.talend.sdk.component.api.component.MigrationHandler;

import java.util.HashMap;
import java.util.Map;

public class DSEMigration implements MigrationHandler {
    @Override
    public Map<String, String> migrate(int incomingVersion, Map<String, String> incomingData) {


        return incomingData;
    }
}
