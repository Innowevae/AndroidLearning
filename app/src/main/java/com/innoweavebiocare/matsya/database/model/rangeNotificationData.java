package com.innoweavebiocare.matsya.database.model;

public class rangeNotificationData {
    public static final String TABLE_NAME = "rangeNotificationData";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_PARAMETER = "parameter";
    public static final String COLUMN_MIN = "min";
    public static final String COLUMN_MAX = "max";

    private String id;
    private final String parameter;
    private final float min;
    private final float max;

    // Create table SQL query
    public static final String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "("
                    + COLUMN_ID + " INT, "
                    + COLUMN_PARAMETER +  " VARCHAR(255), "
                    + COLUMN_MIN + " FLOAT, "
                    + COLUMN_MAX + " FLOAT, "
                    + "CONSTRAINT PK_rangeNotificationData PRIMARY KEY (" + COLUMN_ID + " )"
                    + ")";

    // TODO: Make this function and Calss null safe (So that we can have null data in db
    public rangeNotificationData(String id, String parameter, float min, float max)
    {
        this.id = id;
        this.parameter = parameter;
        this.min = min;
        this.max = max;
    }

    public String getColumnId() {
        return id;
    }

    public String getParam() {
        return parameter;
    }

    public float getMin() {
        return min;
    }

    public float getMax() {
        return max;
    }
}
