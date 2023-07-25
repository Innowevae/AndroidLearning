package com.innoweavebiocare.matsya.database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.util.Pair;
import com.innoweavebiocare.matsya.database.model.alarmData;
import com.innoweavebiocare.matsya.database.model.deviceID;
import com.innoweavebiocare.matsya.database.model.optimalValue;
import com.innoweavebiocare.matsya.database.model.rangeNotificationData;
import com.innoweavebiocare.matsya.database.model.sensorData;
import com.innoweavebiocare.matsya.database.model.userData;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    // Database Version

    private static final int DATABASE_VERSION = 6;

    // Database Name
    private static final String DATABASE_NAME = "MATSYA_DB";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        // create notes table
        db.execSQL(sensorData.CREATE_TABLE);
        db.execSQL(deviceID.CREATE_TABLE);
        db.execSQL(userData.CREATE_TABLE);
        db.execSQL(alarmData.CREATE_TABLE);
        db.execSQL(optimalValue.CREATE_TABLE);
        db.execSQL(rangeNotificationData.CREATE_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + sensorData.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + deviceID.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + userData.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + alarmData.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + optimalValue.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + rangeNotificationData.TABLE_NAME);

        // Create tables again
        onCreate(db);
    }

    public boolean insertSensorData(List<sensorData> sensorDataList) {
        // Assume we will  write the complete data in dB without errors
        boolean retVal = true;

        // get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();

        for (sensorData sensordata : sensorDataList) {
            ContentValues values = new ContentValues();
            values.put(sensorData.COLUMN_DEVICE_ID, sensordata.getId());
            values.put(sensorData.COLUMN_TEMP, sensordata.getSenseTemp());
            values.put(sensorData.COLUMN_PH, sensordata.getSensePH());
            values.put(sensorData.COLUMN_DO, sensordata.getSenseDO());
            values.put(sensorData.COLUMN_TDS, sensordata.getSenseTDS());
            values.put(sensorData.COLUMN_TIMESTAMP, sensordata.getSenseTimeStamp());

            // insert row
            long id = db.insert(sensorData.TABLE_NAME, null, values);

            if (id == -1) {
                // If Unable to put data in dB change retVal
                retVal = false;
            }
        }

        // close db connection
//        db.close();

        // Return whether success or failure
        return retVal;
    }

    public boolean insertAlarmData(List<alarmData> alarmDataList) {
        boolean retVal = true;
        SQLiteDatabase db = this.getWritableDatabase();

        for (alarmData alarmdata : alarmDataList) {
            ContentValues values = new ContentValues();
            values.put(alarmData.COLUMN_EVENT_ID, alarmdata.getEventid());
            values.put(alarmData.COLUMN_MAC_ID, alarmdata.getMacid());
            values.put(alarmData.COLUMN_TIMESTAMP, alarmdata.getTimestamp());

            long id = db.insert(alarmData.TABLE_NAME, null, values);

            if (id == -1) {
                // If Unable to put data in dB change retVal
                retVal = false;
            }
        }
        db.close();
        return retVal;

    }

    public boolean insertRegisteredDevices(List<deviceID> deviceIDList) {
        boolean retVal = true;
        SQLiteDatabase db = this.getWritableDatabase();

        for (deviceID deviceid : deviceIDList) {
            ContentValues values = new ContentValues();
            values.put(deviceID.COLUMN_MAC_ID, deviceid.getId());
            values.put(deviceID.COLUMN_EMAIL, deviceid.getEmail());
            values.put(deviceID.COLUMN_POND_NO, deviceid.getPondName());

            long id = db.insert(deviceID.TABLE_NAME, null, values);

            if (id == -1) {
                // If Unable to put data in dB change retVal
                retVal = false;
            }
        }
        db.close();
        return retVal;

    }

    public boolean insertRegisteredDevice(deviceID newDeviceID) {
        boolean retVal = true;
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(deviceID.COLUMN_MAC_ID, newDeviceID.getId());
        values.put(deviceID.COLUMN_EMAIL, newDeviceID.getEmail());
        values.put(deviceID.COLUMN_POND_NO, newDeviceID.getPondName());

        long id = db.insert(deviceID.TABLE_NAME, null, values);

        if (id == -1) {
            // If Unable to put data in dB change retVal
            retVal = false;
        }
        db.close();
        return retVal;

    }

    public boolean insertUserData(List<userData> userDataList) {
        boolean retVal = true;
        SQLiteDatabase db = this.getWritableDatabase();

        for (userData userdata : userDataList) {
            ContentValues values = new ContentValues();
            values.put(userData.COLUMN_NAME, userdata.getUsername());
            values.put(userData.COLUMN_EMAIL, userdata.getEmail());
            values.put(userData.COLUMN_PHONE_NO, userdata.getPhoneno());
            values.put(userData.COLUMN_ANDROID_ID, userdata.getAndroidid());
            long id = db.insert(userData.TABLE_NAME, null, values);

            if (id == -1) {
                // If Unable to put data in dB change retVal
                retVal = false;
            }
        }
        db.close();
        return retVal;
    }

    public List<sensorData> getSensorData(String deviceID, long startTime, long stopTime) {
        List<sensorData> completeSensorData = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT * FROM " + sensorData.TABLE_NAME + " WHERE " +
                sensorData.COLUMN_DEVICE_ID + " =  \"" + deviceID + "\"" + " AND " +
                sensorData.COLUMN_TIMESTAMP + " BETWEEN " + startTime + " AND " + stopTime +
                " ORDER BY " + sensorData.COLUMN_TIMESTAMP + " DESC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                sensorData sensordata = new sensorData(
                        cursor.getString(cursor.getColumnIndexOrThrow(sensorData.COLUMN_DEVICE_ID)),
                        cursor.getFloat(cursor.getColumnIndexOrThrow(sensorData.COLUMN_TEMP)),
                        cursor.getFloat(cursor.getColumnIndexOrThrow(sensorData.COLUMN_PH)),
                        cursor.getFloat(cursor.getColumnIndexOrThrow(sensorData.COLUMN_DO)),
                        cursor.getFloat(cursor.getColumnIndexOrThrow(sensorData.COLUMN_TDS)),
                        cursor.getLong(cursor.getColumnIndexOrThrow(sensorData.COLUMN_TIMESTAMP))
                );
                completeSensorData.add(sensordata);
            }
            while (cursor.moveToNext());
        }

        // close db connection
        db.close();

        // return sensorData list
        return completeSensorData;
    }

    public List<sensorData> getAllSensorData() {
        List<sensorData> completeSensorData = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + sensorData.TABLE_NAME + " ORDER BY " +
                sensorData.COLUMN_TIMESTAMP + " DESC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                sensorData sensordata = new sensorData(
                        cursor.getString(cursor.getColumnIndexOrThrow(sensorData.COLUMN_DEVICE_ID)),
                        cursor.getFloat(cursor.getColumnIndexOrThrow(sensorData.COLUMN_TEMP)),
                        cursor.getFloat(cursor.getColumnIndexOrThrow(sensorData.COLUMN_PH)),
                        cursor.getFloat(cursor.getColumnIndexOrThrow(sensorData.COLUMN_DO)),
                        cursor.getFloat(cursor.getColumnIndexOrThrow(sensorData.COLUMN_TDS)),
                        cursor.getLong(cursor.getColumnIndexOrThrow(sensorData.COLUMN_TIMESTAMP))
                );
                completeSensorData.add(sensordata);
            }
            while (cursor.moveToNext());
        }

        // close db connection
        db.close();

        // return sensorData list
        return completeSensorData;
    }

    public int getSensorDataCount() {
        int count = 0;
        // SQL Query
        String countQuery = "SELECT COUNT(*) FROM " + sensorData.TABLE_NAME;

        // Run the Query
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            count = cursor.getInt(0);
        }

        // Close the Cursor
        cursor.close();
        // return count
        return count;
    }

    public int getRegisteredDeviceCount() {
        int deviceCount = 0;
        String countQuery = "SELECT COUNT(*) FROM " + deviceID.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            deviceCount = cursor.getInt(0);
        }
        cursor.close();
        return deviceCount;
    }

    // get Latest timestamp
    public sensorData getLatestSensorData(String deviceID) {
        // Local veriable for data
        String deviceId = "";
        float temp = 0;
        float pH = 0;
        float d0 = 0;
        float tds = 0;
        long timeStamp = 0;
        // Select All Query
        String selectQuery = "SELECT  * FROM " + sensorData.TABLE_NAME + " WHERE " +
                sensorData.COLUMN_TIMESTAMP + " = " + getLatestTimeStamp(deviceID) + " AND " +
                sensorData.COLUMN_DEVICE_ID + " =  \"" + deviceID + "\"";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            deviceId = cursor.getString(cursor.getColumnIndexOrThrow(sensorData.COLUMN_DEVICE_ID));
            temp = cursor.getFloat(cursor.getColumnIndexOrThrow(sensorData.COLUMN_TEMP));
            pH = cursor.getFloat(cursor.getColumnIndexOrThrow(sensorData.COLUMN_PH));
            d0 = cursor.getFloat(cursor.getColumnIndexOrThrow(sensorData.COLUMN_DO));
            tds = cursor.getFloat(cursor.getColumnIndexOrThrow(sensorData.COLUMN_TDS));
            timeStamp = cursor.getLong(cursor.getColumnIndexOrThrow(sensorData.COLUMN_TIMESTAMP));
        }
        Log.i("MyAmplifyApp", "Requesting Latest data : " + deviceID + temp + pH + d0 + tds + timeStamp);

        sensorData sensordata = new sensorData(deviceId, temp, pH, d0, tds, timeStamp);
        // close db connection
        db.close();

        // return sensorData list
        return sensordata;
    }


    //get minimum sensor value with timestamp
    public Pair<Double, Long> getMinSensorDataWithTime(String deviceID, String columnID, long startTime, long stopTime) {
        double minSensorData = 0d;
        long timeStamp = 0;
        String selectQuery = "SELECT  *, MIN(" + columnID + ") FROM " + sensorData.TABLE_NAME + " WHERE " +
                sensorData.COLUMN_DEVICE_ID + " =  \"" + deviceID + "\"" + " AND " +
                columnID + "> 0" + " AND " +
                sensorData.COLUMN_TIMESTAMP + " BETWEEN " + startTime + " AND " + stopTime +
                " ORDER BY " + sensorData.COLUMN_TIMESTAMP + " DESC";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            minSensorData = cursor.getDouble(cursor.getColumnIndexOrThrow("MIN(" + columnID + ")"));
            timeStamp = cursor.getLong(cursor.getColumnIndexOrThrow(sensorData.COLUMN_TIMESTAMP));
        }
        cursor.close();
        return new Pair<>(minSensorData, timeStamp);
    }

    //get maximum sensor value with timestamp
    public Pair<Double, Long> getMaxSensorDataWithTime(String deviceID, String columnID, long startTime, long stopTime) {
        double maxSensorData = 0d;
        long timeStamp = 0;
        String selectQuery = "SELECT  *, MAX(" + columnID + ") FROM " + sensorData.TABLE_NAME + " WHERE " +
                sensorData.COLUMN_DEVICE_ID + " =  \"" + deviceID + "\"" + " AND " +
                sensorData.COLUMN_TIMESTAMP + " BETWEEN " + startTime + " AND " + stopTime +
                " ORDER BY " + sensorData.COLUMN_TIMESTAMP + " DESC";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            maxSensorData = cursor.getDouble(cursor.getColumnIndexOrThrow("MAX(" + columnID + ")"));
            timeStamp = cursor.getLong(cursor.getColumnIndexOrThrow(sensorData.COLUMN_TIMESTAMP));
        }
        cursor.close();
        return new Pair<>(maxSensorData, timeStamp);
    }

    // Get latest Timestamp
    public long getLatestTimeStamp(String deviceID) {
        long defaultTimeStamp = 1640995200; // mid night Jan 1st 2022 timeStamp
        String latestTimeQuery = "SELECT MAX(timestamp) FROM " + sensorData.TABLE_NAME +
                " where " + sensorData.COLUMN_DEVICE_ID + " =  \"" + deviceID + "\"";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(latestTimeQuery, null);
        if (cursor.moveToFirst()) {
            defaultTimeStamp = cursor.getLong(0);
        }
        cursor.close();
        return defaultTimeStamp;
    }

    public List<alarmData> getAlarmData(String deviceID) {
        List<alarmData> completeAlarmData = new ArrayList<>();
        String alarmQuery = "SELECT TOP 10 * FROM " + alarmData.COLUMN_EVENT_ID +
                " WHERE " + sensorData.COLUMN_DEVICE_ID + " =  \"" + deviceID + "\"" +
                " ORDER BY " + sensorData.COLUMN_TIMESTAMP + " DESC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(alarmQuery, null);
        if (cursor.moveToFirst()) {
            do {
                alarmData alarmdata = new alarmData(
                        cursor.getString(cursor.getColumnIndexOrThrow(alarmData.COLUMN_MAC_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(alarmData.COLUMN_TIMESTAMP)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(alarmData.COLUMN_EVENT_ID))
                );
                completeAlarmData.add(alarmdata);
            }
            while (cursor.moveToNext());
        }

        // close db connection
        db.close();

        // return sensorData list
        return completeAlarmData;
    }

    public List<userData> getAllUserData() {
        List<userData> completeUserData = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + userData.TABLE_NAME;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                userData userdata = new userData(
                        cursor.getString(cursor.getColumnIndexOrThrow(userData.COLUMN_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(userData.COLUMN_EMAIL)),
                        cursor.getString(cursor.getColumnIndexOrThrow(userData.COLUMN_PHONE_NO)),
                        cursor.getString(cursor.getColumnIndexOrThrow(userData.COLUMN_ANDROID_ID))
                );
                completeUserData.add(userdata);
            }
            while (cursor.moveToNext());
        }
        db.close();
        return completeUserData;
    }

    public void updateUserPhone(String phone, String username) {
        String updateQuery = "UPDATE " + userData.TABLE_NAME +
                " SET " + userData.COLUMN_PHONE_NO + " = " + phone +
                " WHERE " + userData.COLUMN_NAME + " = " + username;
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(updateQuery);
        db.close();
    }

    public void deleteAllSensorData() {
        String deleteQuery = "DELETE FROM " + sensorData.TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(deleteQuery);
        db.close();
    }

    public void deleteDeviceSensorData(String deviceId) {
        String deleteQuery = "DELETE FROM " + sensorData.TABLE_NAME +
                " WHERE " + sensorData.COLUMN_DEVICE_ID + " =  \"" + deviceId + "\"";
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(deleteQuery);
        db.close();
    }

    public void deletePastSensorData(long timeStamp) {
        String deleteQuery = "DELETE FROM " + sensorData.TABLE_NAME +
                " WHERE " + sensorData.COLUMN_TIMESTAMP + " <= " + timeStamp;
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(deleteQuery);
        db.close();
    }

    public void deleteAllAlarmData() {
        String deleteQuery = "DELETE FROM " + alarmData.TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(deleteQuery);
        db.close();
    }

    public void deleteAllAlarmData(String deviceID) {
        String deleteQuery = "DELETE FROM " + alarmData.TABLE_NAME +
                " WHERE " + sensorData.COLUMN_DEVICE_ID + " =  \"" + deviceID + "\"";
        ;
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(deleteQuery);
        db.close();
    }

    public void deleteAllAlarmData(long timeStamp) {
        String deleteQuery = "DELETE FROM " + alarmData.TABLE_NAME +
                " WHERE " + sensorData.COLUMN_TIMESTAMP + " = " + timeStamp;
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(deleteQuery);
        db.close();
    }

    public void deleteAllRegisteredDevice() {
        String deleteQuery = "DELETE FROM " + deviceID.TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(deleteQuery);
        deleteAllAlarmData();
        deleteAllSensorData();
        db.close();
    }

    public void deleteAllRegisteredDevice(String deviceid) {
        String deleteQuery = "DELETE FROM " + deviceID.TABLE_NAME +
                " WHERE " + sensorData.COLUMN_DEVICE_ID + " =  \"" + deviceid + "\"";
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(deleteQuery);
        deleteAllAlarmData(deviceid);
        deleteDeviceSensorData(deviceid);
        db.close();
    }

    public void deleteAllUserData() {
        String deleteQuery = "DELETE FROM " + userData.TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(deleteQuery);
        db.close();
    }

    public List<deviceID> getAllRegisteredDeviceIDs() {
        List<deviceID> registeredDeviceIDs = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + deviceID.TABLE_NAME;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                deviceID registeredDevice = new deviceID(
                        cursor.getString(cursor.getColumnIndexOrThrow(deviceID.COLUMN_MAC_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(deviceID.COLUMN_EMAIL)),
                        cursor.getString(cursor.getColumnIndexOrThrow(deviceID.COLUMN_POND_NO))
                );
                registeredDeviceIDs.add(registeredDevice);
            }
            while (cursor.moveToNext());
        }

        // close db connection
        db.close();

        // return sensorData list
        return registeredDeviceIDs;
    }

    public boolean insertRangeNotifyData(rangeNotificationData rangeNotifyData) {
        // Assume we will  write the complete data in dB without errors
        boolean retVal = true;

        // get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(rangeNotificationData.COLUMN_ID, rangeNotifyData.getColumnId());
        values.put(rangeNotificationData.COLUMN_PARAMETER, rangeNotifyData.getParam());
        values.put(rangeNotificationData.COLUMN_MIN, rangeNotifyData.getMin());
        values.put(rangeNotificationData.COLUMN_MAX, rangeNotifyData.getMax());

        // insert row
        long id = db.insert(rangeNotificationData.TABLE_NAME, null, values);

        if (id == -1) {
            // If Unable to put data in dB change retVal
            retVal = false;
        }

        // close db connection
//        db.close();

        // Return whether success or failure
        return retVal;
    }

    public void deleteRangeNotify() {
        String deleteQuery = "DELETE FROM " + rangeNotificationData.TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(deleteQuery);
        db.close();
    }

    public void deleteRowRangeNotify(String param) {
        String deleteQuery = "DELETE FROM " + rangeNotificationData.TABLE_NAME +
                " WHERE " + rangeNotificationData.COLUMN_PARAMETER + " =  \"" + param + "\"";
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(deleteQuery);
        db.close();
    }

    public List<rangeNotificationData> getAllRangeNotifyData() {
        List<rangeNotificationData> completeRanges = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + rangeNotificationData.TABLE_NAME + " ORDER BY " +
                rangeNotificationData.COLUMN_ID + " ASC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                rangeNotificationData rangeData = new rangeNotificationData(
                        cursor.getString(cursor.getColumnIndexOrThrow(rangeNotificationData.COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(rangeNotificationData.COLUMN_PARAMETER)),
                        cursor.getFloat(cursor.getColumnIndexOrThrow(rangeNotificationData.COLUMN_MIN)),
                        cursor.getFloat(cursor.getColumnIndexOrThrow(rangeNotificationData.COLUMN_MAX))
                );
                completeRanges.add(rangeData);
            }
            while (cursor.moveToNext());
        }

        // close db connection
        db.close();

        // return sensorData list
        return completeRanges;
    }

    public void deleteAllDbData() {
        deleteAllRegisteredDevice();
        deleteRangeNotify();
        deleteAllUserData();
    }
}