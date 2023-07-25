package com.innoweavebiocare.matsya.database.model;

public class userData {

    public static final String TABLE_NAME = "userData";

    public static final String COLUMN_NAME = "UserName";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_PHONE_NO = "PhoneNo";
    public static final String COLUMN_ANDROID_ID= "AndroidId";

    private String username;
    private String email;
    private String phoneno;
    private String androidid;

    // Create table SQL query
    public static final String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "("
                    + COLUMN_NAME + " VARCHAR(255), "
                    + COLUMN_EMAIL + " VARCHAR(255), "
                    + COLUMN_PHONE_NO + " VARCHAR(10), "
                    + COLUMN_ANDROID_ID + " VARCHAR(255), "
                    + "CONSTRAINT PK_registeredDevices PRIMARY KEY (" + COLUMN_EMAIL + "," + COLUMN_ANDROID_ID + ")"
                    + ")";

    public userData(String username, String email, String phoneno, String androidid) {
        this.username=username;
        this.email=email;
        this.phoneno=phoneno;
        this.androidid=androidid;
    }

    public String getUsername(){return username;}
    public String getEmail(){return email;}
    public String getPhoneno(){return phoneno;}
    public String getAndroidid(){return androidid;}

    public void setAndroidId(String toString) {this.androidid = androidid;}

}
