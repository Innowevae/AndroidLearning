package com.innoweavebiocare.matsya.database.model;

public class deviceID {
    public static final String TABLE_NAME = "registeredDevices";

    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_MAC_ID = "macId";
    public static final String COLUMN_POND_NO = "pondName";

    private String deviceId;
    private String email;
    private String pondName;

    // Create table SQL query
    public static final String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "("
                    + COLUMN_EMAIL + " VARCHAR(255), "
                    + COLUMN_MAC_ID + " VARCHAR(255), "
                    + COLUMN_POND_NO + " INT, "
                    + "CONSTRAINT PK_registeredDevices PRIMARY KEY (" + COLUMN_MAC_ID + "," + COLUMN_EMAIL + ")"
                    + ")";


    public deviceID() {
    }

    // TODO: Make this function and Calss null safe (So that we can have null data in db
    public deviceID(String id, String email, String pondName)
    {
        this.deviceId = id;
        this.email = email;
        this.pondName = pondName;
    }

    public String getId() {
        return deviceId;
    }
    public String getEmail(){return email;}
    public String getPondName(){return pondName;}

    public void setId(String toString) {this.deviceId = deviceId;}
    public void setEmail(String toString){this.email = email;}
    public void setPondName(String toString){ this.pondName = pondName;}
}
