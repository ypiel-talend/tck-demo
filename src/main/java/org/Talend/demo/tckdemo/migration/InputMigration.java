package org.Talend.demo.tckdemo.migration;

import org.talend.sdk.component.api.component.MigrationHandler;

import java.util.Map;

public class InputMigration implements MigrationHandler {
    @Override
    public Map<String, String> migrate(int incomingVersion, Map<String, String> incomingData) {


        return incomingData;
    }
}