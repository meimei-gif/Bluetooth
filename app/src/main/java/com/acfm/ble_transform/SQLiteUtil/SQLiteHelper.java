package com.acfm.ble_transform.SQLiteUtil;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class SQLiteHelper extends SQLiteOpenHelper {
    public static final String  DB_NAME="test.db";
    public static final int     DB_VERSION=1;
    private Context             context;

    public SQLiteHelper(Context context) {
        super(context,DB_NAME,null,DB_VERSION);
        this.context=context;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        //String createTable_BT_information="create table BT_information ( MacAddress varchar(100), Content varchar(100))";
        String createTable = "create table repeater (id integer primary key autoincrement,repeaterId text,high text,temperature text,worktime text,time integer)";
        String safetyHat = "create table safetyHat (id integer,hatId text,signalPath text,rssi text,MAC text primary key,temperature text,high text,power text,time integer,status text,humidity text)";
        String zigBeeSignal = "create table zigBeeSignal (signalPath text primary key,time integer,seq text)";
        String historySafetyHat = "create table historySafetyHat(hatId text,signalPath text,MAC text ,seq text,cmd text, temperature text,high text,power text,time integer)";

        //db.execSQL(createTable_BT_information);
        db.execSQL(createTable);
        db.execSQL(safetyHat);
        db.execSQL(zigBeeSignal);
        db.execSQL(historySafetyHat);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
