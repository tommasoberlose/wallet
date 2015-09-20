package com.nego.money;

import android.content.Context;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.Toast;

import com.nego.money.database.DbAdapter;

import java.util.Calendar;

/**
 * Created by Tommaso on 15/07/2015.
 */
public class Element implements Parcelable {
    private int id;
    private String people;
    private String note;
    private String who;
    private String done;
    private String importo;
    private String group;
    private long datec;
    private long dated;

    public Element(String people, String note, String done, String who, String importo, String group, long datec, long dated){
        this.people = people;
        this.note = note;
        this.who = who;
        this.done = done;
        this.importo = importo;
        this.group = group;
        this.datec = datec;
        this.dated = dated;
    }

    public Element(String people, String note, String done, String who, String importo, long datec, long dated){
        this.people = people;
        this.note = note;
        this.who = who;
        this.done = done;
        this.importo = importo;
        this.group = "null";
        this.datec = datec;
        this.dated = dated;
    }

    public Element(int id, String people, String note, String done, String who, String importo, String group, long datec, long dated){
        this.id = id;
        this.people = people;
        this.note = note;
        this.who = who;
        this.done = done;
        this.importo = importo;
        this.group = group;
        this.datec = datec;
        this.dated = dated;
    }

    public Element(Cursor cursor){
        this.id = cursor.getInt(cursor.getColumnIndex(DbAdapter.KEY_ID));
        this.people = cursor.getString(cursor.getColumnIndex(DbAdapter.KEY_PEOPLE));
        this.note = cursor.getString( cursor.getColumnIndex(DbAdapter.KEY_NOTE) );
        this.who = cursor.getString(cursor.getColumnIndex(DbAdapter.KEY_WHO));
        this.done = cursor.getString(cursor.getColumnIndex(DbAdapter.KEY_DONE));
        this.importo = cursor.getString(cursor.getColumnIndex(DbAdapter.KEY_IMPORTO));
        this.group = cursor.getString(cursor.getColumnIndex(DbAdapter.KEY_GROUP));
        this.datec = cursor.getLong( cursor.getColumnIndex(DbAdapter.KEY_DATEC) );
        this.dated = cursor.getLong( cursor.getColumnIndex(DbAdapter.KEY_DATED) );
    }

    public int getId() {
        return id;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getImporto() {
        return importo;
    }

    public void setImporto(String importo) {
        this.importo = importo;
    }

    public long getDatec() {
        return datec;
    }

    public void setDatec(long datec) {
        this.datec = datec;
    }

    public long getDated() {
        return dated;
    }

    public void setDated(long dated) {
        this.dated = dated;
    }

    public String getNote() {
        return note;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPeople() {
        return people;
    }

    public String getWho() {
        return who;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setPeople(String people) {
        this.people = people;
    }

    public void setWho(String who) {
        this.who = who;
    }

    public String getDone() {
        return done;
    }

    public boolean Done() {
        return done.equals("1");
    }

    public void setDone(boolean done) {
        if (done)
            this.done = "1";
        else
            this.done = "0";
    }

    public boolean Me() {
        return who.equals("1");
    }

    public boolean create_element(Context context, DbAdapter dbHelper) {
        if (dbHelper.createElement(this) > 0) {
            return true;
        }
        return false;
    }

    public boolean update_element(Context context, DbAdapter dbHelper) {
        if (dbHelper.updateElement(this)) {
            return true;
        }
        return false;
    }

    public boolean delete_element(Context context, DbAdapter dbHelper) {
        if (dbHelper.deleteElement(this.getId())) {
            return true;
        }
        return false;
    }

    public boolean undo_delete_element(Context context, DbAdapter dbHelper) {
        if (dbHelper.restoreElement(this) > 0) {
            return true;
        }
        return false;
    }

    public boolean done_element(Context context, DbAdapter dbHelper) {
        Calendar c = Calendar.getInstance();
        this.setDone(true);
        this.setDated(c.getTimeInMillis());
        if (dbHelper.updateElement(this)) {
            return true;
        }
        return false;
    }

    public boolean undo_done_element(Context context, DbAdapter dbHelper) {
        this.setDone(false);
        this.setDated(0);
        if (dbHelper.updateElement(this)) {
            return true;
        }
        return false;
    }

    // PARCELIZZAZIONE

    public static final Parcelable.Creator<Element> CREATOR = new Parcelable.Creator<Element>() {
        public Element createFromParcel(Parcel source) {
            return new Element(source.readInt(), source.readString(), source.readString(), source.readString(), source.readString(), source.readString(), source.readString(), source.readLong(), source.readLong());
        }
        public Element[] newArray(int size) {
            return new Element[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(people);
        dest.writeString(note);
        dest.writeString(done);
        dest.writeString(who);
        dest.writeString(importo);
        dest.writeString(group);
        dest.writeLong(datec);
        dest.writeLong(dated);
    }
}
