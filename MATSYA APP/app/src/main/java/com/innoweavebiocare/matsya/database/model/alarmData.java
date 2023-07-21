package com.innoweavebiocare.matsya.database.model;

public class alarmData {
    public static final String TABLE_NAME = "alarmData";

    public static final String COLUMN_MAC_ID = "macId";
    public static final String COLUMN_TIMESTAMP = "timestamp";
    public static final String COLUMN_EVENT_ID = "eventId";

    private String macid;
    private String timestamp;
    private int eventid;

    // Create table SQL query
    public static final String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "("
                    + COLUMN_MAC_ID + " VARCHAR(255), "
                    + COLUMN_TIMESTAMP + " VARCHAR(255), "
                    + COLUMN_EVENT_ID + " INT, "
                    + "CONSTRAINT PK_registeredDevices PRIMARY KEY (" + COLUMN_MAC_ID + "," + COLUMN_TIMESTAMP + ")"
                    + ")";

    public alarmData(String macid, String timestamp, int eventid) {
        this.macid=macid;
        this.timestamp=timestamp;
        this.eventid=eventid;
    }


    public String getMacid(){return macid;}
    public String getTimestamp(){return timestamp;}
    public int getEventid(){return eventid;}
}
