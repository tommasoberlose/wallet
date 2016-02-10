package com.nego.money.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.nego.money.Element;


public class DbAdapter {


    private Context context;
    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;

    // Database fields
    private static final String DATABASE_TABLE = "list";

    public static final String KEY_ID = "id";
    public static final String KEY_PEOPLE = "people";
    public static final String KEY_NOTE = "note";
    public static final String KEY_WHO = "who";
    public static final String KEY_DONE = "done";
    public static final String KEY_IMPORTO = "importo";
    public static final String KEY_GROUP = "lista";
    public static final String KEY_DATEC = "datec";
    public static final String KEY_DATED = "dated";

    public DbAdapter(Context context) {
        this.context = context;
    }

    public DbAdapter open() throws SQLException {
        dbHelper = new DatabaseHelper(context);
        database = dbHelper.getWritableDatabase();
        dbHelper.onCreate(database);
        return this;
    }

    public void close() {
        dbHelper.close();
    }

    private ContentValues createContentValues(int id, String people, String done, String note, String who, String group, String importo, long datec, long dated) {
        ContentValues values = new ContentValues();
        values.put(KEY_ID, id);
        values.put(KEY_PEOPLE, people);
        values.put(KEY_DONE, done);
        values.put(KEY_NOTE, note);
        values.put(KEY_WHO, who);
        values.put(KEY_GROUP, group);
        values.put(KEY_IMPORTO, importo);
        values.put(KEY_DATEC, datec);
        values.put(KEY_DATED, dated);

        return values;
    }

    private ContentValues createContentValues(String people, String done, String note, String who, String group, String importo, long datec, long dated) {
        ContentValues values = new ContentValues();
        values.put(KEY_PEOPLE, people);
        values.put(KEY_DONE, done);
        values.put(KEY_NOTE, note);
        values.put(KEY_WHO, who);
        values.put(KEY_GROUP, group);
        values.put(KEY_IMPORTO, importo);
        values.put(KEY_DATEC, datec);
        values.put(KEY_DATED, dated);

        return values;
    }

    public long createElement(Element e) {
        ContentValues initialValues = createContentValues(e.getPeople(), e.getDone(), e.getNote(), e.getWho(), e.getGroup(), e.getImporto(), e.getDatec(), e.getDated());
        return database.insertOrThrow(DATABASE_TABLE, null, initialValues);
    }

    public long restoreElement(Element e) {
        ContentValues initialValues = createContentValues(e.getId(), e.getPeople(), e.getDone(), e.getNote(), e.getWho(), e.getGroup(), e.getImporto(), e.getDatec(), e.getDated());
        return database.insertOrThrow(DATABASE_TABLE, null, initialValues);
    }

    public boolean updateElement(Element e) {
        ContentValues updateValues = createContentValues(e.getId(), e.getPeople(), e.getDone(), e.getNote(), e.getWho(), e.getGroup(), e.getImporto(), e.getDatec(), e.getDated());
        return database.update(DATABASE_TABLE, updateValues, KEY_ID + "==" + e.getId(), null) > 0;
    }

    public boolean deleteElement(int ID) {
        return database.delete(DATABASE_TABLE, KEY_ID + "==" + ID, null) > 0;
    }

    public Cursor fetchAllElements() {
        return database.query(DATABASE_TABLE, new String[]{KEY_ID, KEY_PEOPLE, KEY_NOTE, KEY_WHO, KEY_DONE, KEY_IMPORTO, KEY_GROUP, KEY_DATEC, KEY_DATED},
                KEY_DONE + " != 1 ", null, null, null, KEY_DATEC + " DESC");
    }

    public Cursor fetchAllElementsOld() {
        return database.query(DATABASE_TABLE, new String[]{KEY_ID, KEY_PEOPLE, KEY_NOTE, KEY_WHO, KEY_DONE, KEY_IMPORTO, KEY_GROUP, KEY_DATEC, KEY_DATED},
                KEY_DONE + " == 1 ", null, null, null, KEY_DATEC + " DESC");
    }

    public Cursor fetchAllElementsAll() {
        return database.query(DATABASE_TABLE, new String[]{KEY_ID, KEY_PEOPLE, KEY_NOTE, KEY_WHO, KEY_DONE, KEY_IMPORTO, KEY_GROUP, KEY_DATEC, KEY_DATED},
                null, null, null, null, KEY_DATEC + " DESC");
    }

    public Cursor fetchElementsByFilterPeople(String filter) {
        Cursor mCursor = database.query(true, DATABASE_TABLE, new String[]{
                        KEY_ID, KEY_PEOPLE, KEY_NOTE, KEY_WHO, KEY_DONE, KEY_IMPORTO, KEY_GROUP, KEY_DATEC, KEY_DATED},
                KEY_PEOPLE + " like '%" + filter + "%' AND " + KEY_DONE + " != 1 ", null, null, null,  KEY_DATEC + " DESC", null);

        return mCursor;
    }

    public Cursor fetchElementsByFilterPeopleOld(String filter) {
        Cursor mCursor = database.query(true, DATABASE_TABLE, new String[]{
                        KEY_ID, KEY_PEOPLE, KEY_NOTE, KEY_WHO, KEY_DONE, KEY_IMPORTO, KEY_GROUP, KEY_DATEC, KEY_DATED},
                KEY_PEOPLE + " like '%" + filter + "%' AND " + KEY_DONE + " == 1 ", null, null, null,  KEY_DATEC + " DESC", null);

        return mCursor;
    }

    public Cursor fetchElementsByFilterPeopleAll(String filter) {
        Cursor mCursor = database.query(true, DATABASE_TABLE, new String[]{
                        KEY_ID, KEY_PEOPLE, KEY_NOTE, KEY_WHO, KEY_DONE, KEY_IMPORTO, KEY_GROUP, KEY_DATEC, KEY_DATED},
                KEY_PEOPLE + " like '%" + filter + "%'", null, null, null,  KEY_DATEC + " DESC", null);

        return mCursor;
    }

    public Cursor getElementById(int id) {
        Cursor mCursor = database.query(true, DATABASE_TABLE, new String[]{
                        KEY_ID, KEY_PEOPLE, KEY_NOTE, KEY_WHO, KEY_DONE, KEY_IMPORTO, KEY_GROUP, KEY_DATEC, KEY_DATED},
                KEY_ID + " == '" + id + "'", null, null, null, KEY_DATEC + " DESC", null);

        return mCursor;
    }
}