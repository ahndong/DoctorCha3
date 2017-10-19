package com.infoline.doctorcha.core.DataBase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2016-03-11.
 */
public class LocalDBManager extends SQLiteOpenHelper {

    public LocalDBManager(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //db.execSQL("CREATE TABLE FAVORITE_CATEGORY( _id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, price INTEGER);");
        //db.execSQL("CREATE TABLE FAVORITE_CATEGORY( _id INTEGER PRIMARY KEY, nm TEXT, tp INTEGER, res INTEGER, sn INTEGER);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
