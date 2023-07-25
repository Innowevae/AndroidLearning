package com.innoweavebiocare.matsya.database.model;

public class sensorData {
    public static final String TABLE_NAME = "sensorData";

    public static final String COLUMN_DEVICE_ID = "id";
    public static final String COLUMN_TEMP = "TEMP";
    public static final String COLUMN_PH = "PH";
    public static final String COLUMN_DO = "DO";
    public static final String COLUMN_TDS = "TDS";
    public static final String COLUMN_TIMESTAMP = "timestamp";

    private String id;
    private float senseTemp;
    private float sensePH;
    private float senseDO;
    private float senseTDS;
    private long senseTimeStamp;

    // Create table SQL query
    public static final String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "("
                    + COLUMN_DEVICE_ID + " VARCHAR(255), "
                    + COLUMN_TEMP + " FLOAT,"
                    + COLUMN_PH + " FLOAT,"
                    + COLUMN_DO + " FLOAT,"
                    + COLUMN_TDS + " FLOAT,"
                    + COLUMN_TIMESTAMP + " TIMESTAMP NOT NULL, "
                    + "CONSTRAINT PK_sensorData PRIMARY KEY (" + COLUMN_DEVICE_ID + "," + COLUMN_TIMESTAMP + ")"
                    + ")";

    // TODO: Make this function and Calss null safe (So that we can have null data in db
    public sensorData(String id, float senseTemp, float sensePH, float senseDO, float senseTDS, long senseTimeStamp)
    {
        this.senseTemp = senseTemp;
        this.sensePH = sensePH;
        this.senseDO = senseDO;
        this.senseTDS = senseTDS;
        this.senseTimeStamp = senseTimeStamp;
        this.id = id;
    }

    public String getId() {
        return id;
    }
    public float getSenseTemp() {
        return senseTemp;
    }
    public float getSensePH() {
        return sensePH;
    }
    public float getSenseDO() {
        return senseDO;
    }
    public float getSenseTDS() {
        return senseTDS;
    }
    public long getSenseTimeStamp() { return senseTimeStamp; }

    public void setTemp(float senseTemp) {
        this.senseTemp = senseTemp;
    }
    public void setTDS(float senseTDS) {
        this.senseTDS = senseTDS;
    }
    public void setDO(float senseDO) {
        this.senseDO = senseDO;
    }
    public void setPH(float sensePH) {
        this.sensePH = sensePH;
    }
    public void setTimeStamp(long senseTimeStamp) {
        this.senseTimeStamp = senseTimeStamp;
    }

}
