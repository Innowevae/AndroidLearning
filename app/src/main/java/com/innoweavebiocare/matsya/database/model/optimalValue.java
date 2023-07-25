package com.innoweavebiocare.matsya.database.model;

public class optimalValue {
    public static final String TABLE_NAME = "optimalValue";

    public static final String COLUMN_PARAMS = "params";
    public static final String COLUMN_MIN = "min";
    public static final String COLUMN_MAX = "max";

    // Create table SQL query
    public static final String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "("
                    + COLUMN_PARAMS + " VARCHAR(255), "
                    + COLUMN_MIN + " FLOAT, "
                    + COLUMN_MAX + " FLOAT, "
                    + "CONSTRAINT PK_registeredDevices PRIMARY KEY (" + COLUMN_PARAMS + ")"
                    + ")";

}
