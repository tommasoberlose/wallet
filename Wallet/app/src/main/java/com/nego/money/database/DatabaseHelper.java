package com.nego.money.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "listdb";
    private static final int DATABASE_VERSION = 3;

    private static final String DATABASE_CREATE = "create table IF NOT EXISTS list (id integer primary key autoincrement, done text not null, people text not null, note text not null, who text not null, importo text not null, lista text not null, datec long not null, dated long not null);";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade( SQLiteDatabase database, int oldVersion, int newVersion ) {

        database.execSQL("DROP TABLE IF EXISTS list");
        onCreate(database);

    }
}